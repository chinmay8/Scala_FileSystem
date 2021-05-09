package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.files.{DirEntry, Directory}
import com.rtjvm.scala.oop.filesystem.State

import scala.annotation.tailrec

/*
@ Author : Chinmay Chaudhari
  Date : 04-05-2021
*/

class Cd(dir: String) extends Command {
  override def apply(state: State): State = {

    /*
    cd /something/somethingElse/.../
    cd a/b/c/ = relative to the current working directory

    cd ..
    cd .

    cd a/./.././a/
     */

    // 1. find the root
    val root = state.root
    val wd = state.wd

    // 2. find the absolute path of the directory I want to add
    val absolutePath = {
      if (dir.startsWith(Directory.SEPARATOR)) dir
      else if (wd.isRoot) wd.path + dir
      else wd.path + Directory.SEPARATOR + dir
    }

    // 3. find the directory to cd to, given the path
    val destinationDirectory = doFindEntry(root, absolutePath)
    
    // 4. change the state  given the new directory
    if(destinationDirectory == null || !destinationDirectory.isDirectory)
      state.setMessage(dir + ": no such directory")
    else
      State(root, destinationDirectory.asDirectory)

  }


  def doFindEntry(root: Directory, path: String): DirEntry = {

    @tailrec
    def findEntryHelper(currentDirectory: Directory, path: List[String]): DirEntry = {
      if (path.isEmpty || path.head.isEmpty) currentDirectory
      else if (path.tail.isEmpty) currentDirectory.findEntry(path.head)
      else {
        val nextDir = currentDirectory.findEntry(path.head)
        if (nextDir == null) null
        else findEntryHelper(nextDir.asDirectory, path.tail)
      }
    }

    // 1. tokens

    val tokens: List[String] = path.substring(1).split(Directory.SEPARATOR).toList

    // 1.5 eliminate/collapse tokens

    /*
    ["a","b"] => ["a"]
    ["a","b",".","."] => ["a","b"]

    /a/.. => ["a",".."] => []
    /a/b/../ => ["a","b",".."] => ["a"]
     */

    @tailrec
    def collapseRelativeTokens(path: List[String], result: List[String]): List[String] = {
      if (path.isEmpty) result
      else if (".".equals(path.head)) collapseRelativeTokens(path.tail, result)
      else if ("..".equals(path.head)) {
        if (result.isEmpty) null
        else collapseRelativeTokens(path.tail, result.init)
      } else collapseRelativeTokens(path.tail, result :+ path.head)
    }

    val newTokens = collapseRelativeTokens(tokens, List())

    // 2. navigate to the correct entry
    if (newTokens == null) null
    else findEntryHelper(root, newTokens)

  }
}