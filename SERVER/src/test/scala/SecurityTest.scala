import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

import org.scalatest.{Matchers, FlatSpec}
import the.accidental.billionaire.secretchat.security._

/**
 * Created by infinitu on 2015. 4. 17..
 */
class SecurityTest() extends FlatSpec with Matchers{
//
//  "A AddressEncryptor" should "encrypt and decrypt well" in{
//    val secret = "secretKEY1231231"
//    val plainText = "plain"
//    val encrypted:String = {
//      val secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "AES")
//      val encipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
//      encipher.init(Cipher.ENCRYPT_MODE, secretKey)
//      Base64.getEncoder.encodeToString(encipher.doFinal(plainText.getBytes))
//    }
//
//    implicit val udata = Some(UserData("","","",secret))
//    val result = AddressEncryptor.addressEncrypt(plainText)
//    result should be (encrypted)
//
//    AddressEncryptor.addressDecrypt(result) should be (plainText)
//  }


}
