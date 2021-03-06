package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.files.{DirEntry, Directory}
import com.rtjvm.scala.oop.filesystem.State

/*
@ Author : Chinmay Chaudhari
  Date : 02-05-2021
*/

abstract class CreateEntry(name: String) extends Command {

  override def apply(state: State): State = {
    val wd = state.wd
    if (wd.hasEntry(name)) {
      state.setMessage("Entry" + name + " already exists!")
    }else if (name.contains(Directory.SEPARATOR)) {
      state.setMessage(name + " must not contain separators!")
    }else if (checkIllegal(name)) {
      state.setMessage(name + ": illegal entry name!")
    }else {
      doCreateEntry(state, name)
    }
  }

  def checkIllegal(name: String): Boolean = {
    name.contains(".")
  }

  def doCreateEntry(state: State,name: String): State = {

    def updateStructure(currentDirectory: Directory, path: List[String], newEntry: DirEntry): Directory = {
      if (path.isEmpty) currentDirectory.addEntry(newEntry)
      else {
        val oldEntry = currentDirectory.findEntry(path.head)
        currentDirectory.replaceEntry(oldEntry.name, updateStructure(oldEntry.asDirectory, path.tail, newEntry))
      }
    }

    val wd = state.wd

//    1. All the directories in full path
    val allDirsInPath = wd.getAllFoldersInPath

//    2. create new dirctory in the wd
    val newEntry = createSpecificEntry(state)

//    3. update the whle directory structure starting  from the root
//    (the directory structure is IMMUTABLE)
    val newRoot = updateStructure(state.root, allDirsInPath, newEntry)

//    4. find new working directory INSTANCE given wd's full path ,in the NEW directory structure
    val newWd = newRoot.findDescendant(allDirsInPath)

    State(newRoot,newWd)
  }

  def createSpecificEntry(state: State): DirEntry

}
