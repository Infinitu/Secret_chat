package the.accidental.billionaire.secretchat.utils

import java.util.Base64

import scala.util.Random

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

    object KeyGenerator{
    def genRandomHex(cnt:Int) = {
      val buf = new StringBuffer(cnt*2)
      (1 to cnt)
        .map(_=>(Random.nextInt()&0xff).toByte)
        .map("%02X".format(_))
        .foreach(buf.append)
      buf.toString
    }
    def genEncKeyInBase64(cnt:Int) = {
      Base64.getEncoder.encodeToString((1 to cnt).map(_=>(Random.nextInt()&0xff).toByte).toArray)
    }
  }
}
