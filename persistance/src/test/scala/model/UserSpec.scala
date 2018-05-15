package model

class UserSpec extends TestSuite {

  it should "create & get user table in cassandra" in {
    val email = "emailId@knoldus.com"
    val dummyUserInfo = UserInfo(email, "password", Category.Trainee)
    val futureUserInfo = for {
      _ <- database.user.createUser(dummyUserInfo)
      userInfo <- database.user.getUserByEmail(email)
    } yield userInfo
    futureUserInfo.map(userInfo => assert(userInfo === dummyUserInfo))
  }

  it should "update category by email in User" in {
    val email = "emailId@knoldus.com"
    val futureUserInfo = for {
      _ <- database.user.createUser(UserInfo(email, "password", Category.Trainee))
      _ <- database.user.updateCategoryByEmail(email, Category.Admin)
      userInfo <- database.user.getUserByEmail(email)
    } yield userInfo
    futureUserInfo.map(userInfo => assert(userInfo === UserInfo(email, "password", Category.Admin)))
  }

}
