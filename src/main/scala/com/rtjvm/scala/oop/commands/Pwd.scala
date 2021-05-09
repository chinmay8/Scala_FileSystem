package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.filesystem.State

/*
@ Author : Chinmay Chaudhari
  Date : 02-05-2021
*/

class Pwd extends Command {

  override def apply(state: State): State =
    state.setMessage(state.wd.path)

}
