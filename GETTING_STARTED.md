# Getting started

## Tools you will use

### Android Studio

Android Studio is the program where you will work on the game. The game **project** is a folder containing its Java instructions, pictures, and setup files. The Java instructions are called **source code**. Android Studio puts the tools for reading, changing, checking, and running that code in one place. A program that combines these tools is called an **integrated development environment (IDE)**. Despite its name, Android Studio can also make games that run in a window on a computer. You do not need an Android phone or tablet for this course.

### Git

Git is a tool that remembers how your files change. You can ask Git to save a named checkpoint, called a **commit**, after completing a piece of work. If you make a mistake later, those checkpoints help you compare versions and recover earlier work. Keeping this history is called **version control**.

A project folder tracked by Git is called a **repository**. Git and GitHub are different: Git tracks the repository on your computer, while GitHub is a website that can store and share a copy online. You will use Git by clicking commands in Android Studio.

### libGDX

libGDX is a **library**, which means it is a collection of ready-made code that another project can use. It provides the game window, tools for drawing pictures, and a way to detect keyboard input. It also provides the **game loop**, which repeatedly updates what is happening and draws the next picture on the screen. You do not install libGDX separately; the project downloads it when needed.

## Set up the project

### Install Android Studio and Git

Install Android Studio and Git before continuing. You need both programs, but you do not need a GitHub account.

#### Windows

Download Android Studio from its official website:

```text
Web browser
→ Open https://developer.android.com/studio
→ Download Android Studio
→ Accept the terms
→ Download the Windows .exe installer
```

Install Android Studio:

```text
Downloads folder
→ Double-click the Android Studio .exe file
→ Allow it to make changes, if asked
→ Follow the Setup Wizard
→ Keep the recommended options
→ Finish
```

Download and install Git:

```text
Web browser
→ Open https://git-scm.com/install/windows
→ Download Git for Windows
→ Double-click the downloaded installer
→ Keep the default choices
→ Install
→ Finish
```

#### macOS

Download Android Studio from its official website. Choose the download that matches whether your Mac has an Apple or Intel chip:

```text
Web browser
→ Open https://developer.android.com/studio
→ Download Android Studio
→ Choose Apple chip or Intel chip
→ Accept the terms
→ Download the .dmg file
```

Install Android Studio:

```text
Downloads folder
→ Open the Android Studio .dmg file
→ Drag Android Studio into Applications
→ Open Android Studio
→ Complete the Setup Wizard
```

Apple includes Git with its Xcode Command Line Tools. Install those tools with this one terminal command:

```text
Applications
→ Utilities
→ Terminal
→ Enter: xcode-select --install
→ Press Return
→ Install
```

This command only installs the tools that include Git. You will do your Git work by clicking commands in Android Studio, not by entering Git commands in the terminal.

### Clone the project

You will **clone** the project, which means downloading a copy to your computer. Git will then be able to record your own work on that computer.

From the Android Studio welcome screen, clone the course like this:

```text
Get from VCS
→ Version control: Git
→ URL: https://github.com/plutusmaximus/ftc-java-course.git
→ Choose where to save the course on your computer
→ Clone
```

If Android Studio already has another project open, start here instead. On Windows, the **hamburger button** (☰) is in the upper-left corner of the Android Studio window. It's called a hamburger because it resembles a hamburger. On Macs, skip the hamburger-button step below and choose **File** from the menu bar at the top of the screen:

```text
Hamburger button (☰)
→ File
→ New
→ Project from Version Control
→ Version control: Git
→ URL: https://github.com/plutusmaximus/ftc-java-course.git
→ Choose where to save the course on your computer
→ Clone
```

The repository contains the course materials and several game projects. A **directory** is another name for a folder. Open only the starter `game` directory as your Android Studio project:

```text
Hamburger button (☰)
→ File
→ Open
→ Open the cloned ftc-java-course directory
→ Select game
→ Open
→ Trust Project, if prompted
```

**Gradle** is the tool that reads the project's setup files and prepares the project. Android Studio performs a **Gradle sync** to learn how the project is organized. During the first sync, Gradle also downloads the **dependencies**, which are libraries and other outside code the game needs. This may take several minutes.

Wait for the Gradle sync to finish before continuing.

Before changing any code, run the starter. The supplied **run configuration** is a saved set of instructions that tells Android Studio how to start this game:

```text
Main toolbar near the top of the Android Studio window
→ Run-configuration menu
→ RobotGame
→ Green triangular Run button next to the run configuration
```

A blank, dark game window should open. The robot is not visible yet; making it visible is part of the first session. Return to Android Studio and click the red Stop button.

You are now ready to follow [Session 1 — Make the Robot Move](sessions/session-1/TUTORIAL.md). Complete the sessions in order because each one begins with the game produced by the preceding session. Make your changes in `game/`. Use the projects under `reference-games/` only to compare your work with a completed session or recover if you become stuck.

After each session, use Android Studio to create a commit—a named Git checkpoint stored on your computer:

```text
Hamburger button (☰)
→ Git
→ Commit
→ Review and select your game changes
→ Enter a short message describing what you completed
→ Commit
```
