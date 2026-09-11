# Slot Machine Project

> 🚧 **Status: In development.** This project is being built as part of an ongoing course and is not yet finished.

A slot machine simulator, developed as the initial project for the **Object Oriented Development (DOPO-POOB)** course.

## 📚 Academic context

This project was developed at **Escuela Colombiana de Ingeniería Julio Garavito**, as part of the **Object Oriented Development (DOPO-POOB)** course, during the **2026-2** academic term.

## 👥 Contributors

- [Elián Ibarra](https://github.com/FazeElian/)
- [Oscar Poveda](https://github.com/SOYOSCAR1287)

## 🏷️ Development cycles & versioning

This project is developed iteratively across multiple academic cycles. Contributions, refactoring, and features are continuously added during each cycle, and a permanent release tag (e.g., `ciclo-1`, `ciclo-2`) is created at the end of each stage to lock the deliverable version.

### How to view the project at a specific cycle

You can explore or switch your local workspace to any completed cycle using Git tags:

#### 1. List available cycles
```bash
git tag

```

#### 2. Checkout a specific cycle locally

To inspect or run the codebase at the end of a specific cycle, switch to its tag:

```bash
git checkout tags/ciclo-1

```

*(To return to the latest active development branch afterwards, run `git checkout main`).*

#### 3. View on GitHub

You can also browse the codebase for any cycle directly in the browser by selecting the tag from the **Branch/Tag** dropdown menu on GitHub or visiting the repository's **Tags / Releases** section.

---

## 📝 Project description

The purpose of this project is to develop an application that simulates a situation inspired by **Problem I** of the 2025 International Collegiate Programming Contest: *Slot Machine*. In this simulator, symbols are identified by shapes of different colors.

During this first cycle, the goal is to **build the simulator**, without actually solving the marathon problem itself.

### Main features

The simulator allows:

1. Creating a slot machine.
2. Adding or removing a wheel.
3. Adding or removing a symbol.
4. Spinning the machine's wheels.
5. Querying the machine's symbols.
6. Checking whether the current configuration is the winning one.
7. Making the simulator visible or invisible (it must be able to run in invisible mode).
8. Terminating the simulator.

## 🛠️ Build

- The project was developed in **BlueJ**.
- The classes reuse, and when necessary extend, components from the **shapes*- package.
- The design (class and sequence diagrams) was done using the **Astah*- tool.
- Code documentation follows **Javadoc** standards.

## 📂 Repository structure

```
/src            → Project source code (Java classes)
/design         → Class and sequence diagrams (Astah)
/docs           → Additional documentation / retrospective
```

## 🔄 Retrospective

The cycle's retrospective document (defined mini-cycles, current project status, time invested, achievements, technical problems, and references used) can be found in the `/docs` folder.