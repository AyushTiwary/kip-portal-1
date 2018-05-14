package com.knoldus.util

import org.mindrot.jbcrypt.BCrypt


object PassWordUtility {

  def hashedPassword(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())

  }

  def verifyPassword(plainPassword: String, hashedPassword: String): Boolean = {
    BCrypt.checkpw(plainPassword, hashedPassword)


  }

}