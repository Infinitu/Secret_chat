package the.accidental.billionaire.secretchat.security

import java.util.Base64

import the.accidental.billionaire.secretchat.utils.crypto.AES

/**
 * Created by infinitu on 2015. 4. 17..
 */
object AddressEncryptor {
  private val encrypter = AES
  def encodeBase64(bytes: Array[Byte]) = Base64.getEncoder.encodeToString(bytes)
  def decodeBase64(str:String) = Base64.getDecoder.decode(str)
  def addressEncrypt(plainAddress:String)(implicit userData:Option[UserData]):String = userData.map{userData=>
    encodeBase64(encrypter.encrypt(plainAddress.getBytes,userData.addressEncryptKey));
  }.getOrElse(plainAddress)
  def addressDecrypt(encrptedAddress:String)(implicit userData:Option[UserData]):String  = userData.map{userData=>
    new String(encrypter.decrypt(decodeBase64(encrptedAddress),userData.addressEncryptKey));
  }.getOrElse(encrptedAddress)
}

