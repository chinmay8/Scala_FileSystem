package com.rtjvm.scala.oop.files

/*
@ Author : Chinmay Chaudhari
  Date : 25-04-2021
*/

abstract class DirEntry(val parentPath: String,val name: String) {

  def path: String = {
    val separatorIfNecessary =
      if (Directory.ROOT_PATH.equals(parentPath)) ""
      else Directory.SEPARATOR
    parentPath + separatorIfNecessary + name
  }

  def asDirectory: Directory

  def isDirectory: Boolean

  def isFile: Boolean

  def getType: String

  def asFile: File
}
