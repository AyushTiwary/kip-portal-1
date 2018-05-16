package model

import com.knoldus.domains.UserDetails

class UserInfoSpec extends TestSuite {

  it should "create & get user table in cassandra" in {
    val email = "emailId@knoldus.com"
    val dummyUserInfo = UserDetails(email, "password")
    val futureUserInfo = for {
      _ <- database.user.createUser(dummyUserInfo)
      userInfo <- database.user.getUserByEmail(email)
    } yield userInfo
    futureUserInfo.map(userInfo => assert(userInfo === Some(dummyUserInfo)))
  }

  it should "update category by email in User table" in {
    val email = "emailId@knoldus.com"
    val futureUserInfo = for {
      _ <- database.user.createUser(UserDetails(email, "password"))
      _ <- database.user.updateCategoryByEmail(email, "Admin")
      userInfo <- database.user.getUserByEmail(email)
    } yield userInfo
    futureUserInfo.map(userInfo => assert(userInfo === Some(UserDetails(email, "password", Option("Admin")))))
  }

  it should "update password by email in User table" in {
    val email = "emailId@knoldus.com"
    val futureUserInfo = for {
      _ <- database.user.createUser(UserDetails(email, "oldPassword"))
      _ <- database.user.updatePasswordByEmail(email, "newPassword")
      userInfo <- database.user.getUserByEmail(email)
    } yield userInfo
    futureUserInfo.map(userInfo => assert(userInfo === Some(UserDetails(email, "newPassword", None))))
  }

}
