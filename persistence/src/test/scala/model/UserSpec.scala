package model

import com.knoldus.domains.UserResponse

class UserSpec extends TestSuite {

  it should "create & get user table in cassandra" in {
    val email = "emailId@knoldus.com"
    val dummyUserInfo = UserResponse(email, "password")
    val futureUserInfo = for {
      _ <- database.user.createUser(dummyUserInfo)
      userInfo <- database.user.getUserByEmail(email)
    } yield userInfo
    futureUserInfo.map(userInfo => assert(userInfo === dummyUserInfo))
  }

  it should "update category by email in User table" in {
    val email = "emailId@knoldus.com"
    val futureUserInfo = for {
      _ <- database.user.createUser(UserResponse(email, "password"))
      _ <- database.user.updateCategoryByEmail(email, "Admin")
      userInfo <- database.user.getUserByEmail(email)
    } yield userInfo
    futureUserInfo.map(userInfo => assert(userInfo === UserResponse(email, "password", Option("Admin"))))
  }

  it should "update password by email in User table" in {
    val email = "emailId@knoldus.com"
    val futureUserInfo = for {
      _ <- database.user.createUser(UserResponse(email, "oldPassword"))
      _ <- database.user.updatePasswordByEmail(email, "newPassword")
      userInfo <- database.user.getUserByEmail(email)
    } yield userInfo
    futureUserInfo.map(userInfo => assert(userInfo === UserResponse(email, "newPassword", Option("Trainee"))))
  }

}
