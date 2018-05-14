package model

object Category extends Enumeration {
  type Category = Value
  val Root, Admin, Trainer, Trainee = Value
}