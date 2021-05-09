package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.filesystem.State

/*
@ Author : Chinmay Chaudhari
  Date : 25-04-2021
*/

class UnknownCommand extends Command {
  override def apply(state: State): State =
    state.setMessage("Command not found!")
}
