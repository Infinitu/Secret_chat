package the.accidental.billionaire.secretchat.utils

/**
 * Created by infinitu on 2015. 4. 17..
 */
package object crypto {
  import javax.crypto.spec.SecretKeySpec
  import javax.crypto.Cipher

  trait Encryption {
    def encrypt(dataBytes: Array[Byte], secret: String): Array[Byte]
    def decrypt(codeBytes: Array[Byte], secret: String): Array[Byte]
  }

  class JavaCryptoEncryption(algorithmName: String) extends Encryption {

    def encrypt(bytes: Array[Byte], secret: String): Array[Byte] = {
      val secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName)
      val encipher = Cipher.getInstance(algorithmName + "/ECB/PKCS5Padding")
      encipher.init(Cipher.ENCRYPT_MODE, secretKey)
      encipher.doFinal(bytes)
    }

    def decrypt(bytes: Array[Byte], secret: String): Array[Byte] = {
      val secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName)
      val encipher = Cipher.getInstance(algorithmName + "/ECB/PKCS5Padding")
      encipher.init(Cipher.DECRYPT_MODE, secretKey)
      encipher.doFinal(bytes)
    }
  }

  object DES extends JavaCryptoEncryption("DES")
  object AES extends JavaCryptoEncryption("AES")
}
