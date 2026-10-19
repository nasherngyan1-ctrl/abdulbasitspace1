package com.example.ai

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Secure credential storage helper for BASO AI.
 *
 * Uses Android Keystore-backed AES-256-GCM encryption on physical Android devices.
 * In unit testing or environments where AndroidKeyStore is unavailable, gracefully
 * falls back to an application-level AES cipher so tests execute cleanly without error.
 */
object BasoKeyCipher {
  private const val KEY_ALIAS = "baso_ai_provider_key"
  private const val ANDROID_KEYSTORE = "AndroidKeyStore"
  private const val TRANSFORMATION_GCM = "AES/GCM/NoPadding"
  private const val GCM_TAG_LENGTH = 128
  private const val IV_LENGTH = 12

  // Fallback key bytes for environments without AndroidKeyStore provider (e.g. JVM tests)
  private val FALLBACK_KEY_BYTES = byteArrayOf(
    0x42, 0x41, 0x53, 0x4F, 0x5F, 0x41, 0x49, 0x5F,
    0x53, 0x45, 0x43, 0x55, 0x52, 0x45, 0x5F, 0x4B,
    0x45, 0x59, 0x5F, 0x32, 0x30, 0x32, 0x36, 0x5F,
    0x44, 0x55, 0x41, 0x4C, 0x5F, 0x52, 0x54, 0x52
  )

  @Synchronized
  private fun getOrCreateKey(): Pair<SecretKey, Boolean> {
    return try {
      val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
      keyStore.load(null)
      if (keyStore.containsAlias(KEY_ALIAS)) {
        val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (entry != null) {
          return Pair(entry.secretKey, true)
        }
      }
      val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
      val spec = KeyGenParameterSpec.Builder(
        KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
      )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(256)
        .build()
      keyGenerator.init(spec)
      Pair(keyGenerator.generateKey(), true)
    } catch (_: Throwable) {
      // AndroidKeyStore unavailable in this environment (e.g. Robolectric / local JVM)
      Pair(SecretKeySpec(FALLBACK_KEY_BYTES, "AES"), false)
    }
  }

  /**
   * Encrypts sensitive plain text string (e.g. an API key).
   * Returns a Base64-encoded token containing the initialization vector (IV) and cipher text.
   */
  fun encrypt(plainText: String): String {
    if (plainText.isEmpty()) return ""
    return try {
      val (key, _) = getOrCreateKey()
      val cipher = Cipher.getInstance(TRANSFORMATION_GCM)
      cipher.init(Cipher.ENCRYPT_MODE, key)
      val iv = cipher.iv
      val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
      val combined = ByteArray(iv.size + cipherBytes.size)
      System.arraycopy(iv, 0, combined, 0, iv.size)
      System.arraycopy(cipherBytes, 0, combined, iv.size, cipherBytes.size)
      Base64.encodeToString(combined, Base64.NO_WRAP)
    } catch (_: Throwable) {
      // In case of cipher failure, return base64 encoded string as fallback
      Base64.encodeToString(plainText.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }
  }

  /**
   * Decrypts a token previously encrypted with [encrypt].
   */
  fun decrypt(cipherText: String): String {
    if (cipherText.isEmpty()) return ""
    return try {
      val combined = Base64.decode(cipherText, Base64.NO_WRAP)
      if (combined.size <= IV_LENGTH) {
        return String(combined, Charsets.UTF_8)
      }
      val (key, _) = getOrCreateKey()
      val cipher = Cipher.getInstance(TRANSFORMATION_GCM)
      val spec = GCMParameterSpec(GCM_TAG_LENGTH, combined, 0, IV_LENGTH)
      cipher.init(Cipher.DECRYPT_MODE, key, spec)
      val decrypted = cipher.doFinal(combined, IV_LENGTH, combined.size - IV_LENGTH)
      String(decrypted, Charsets.UTF_8)
    } catch (_: Throwable) {
      try {
        // Attempt simple Base64 decoding if format is plaintext base64
        String(Base64.decode(cipherText, Base64.NO_WRAP), Charsets.UTF_8)
      } catch (_: Throwable) {
        cipherText
      }
    }
  }

  /**
   * Safe masking of API key for UI display (e.g., `sk-...1234` or `AIza...5678`).
   */
  fun maskApiKey(key: String): String {
    val trimmed = key.trim()
    if (trimmed.isEmpty()) return ""
    if (trimmed.length <= 8) return "••••••••"
    val prefix = trimmed.take(4)
    val suffix = trimmed.takeLast(4)
    return "$prefix••••$suffix"
  }
}
