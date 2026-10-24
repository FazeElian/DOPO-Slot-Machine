# Cycle 5 instructions

## 🗂️ Project folders

The repository has two versions of the project:

- **`DOPO-SlotMachine-BlueJ/`**: everything that was done before **cycle 5**. It was developed and run with the **BlueJ** application.
- **`DOPO-SlotMachine/`**: the project organized as a **Maven** project, with the packages `domain`, `presentation` and `test`, the `pom.xml` and the **JaCoCo** and **PMD** reports.

## 📊 Reports (JaCoCo and PMD)

The reports are in `DOPO-SlotMachine/informes/`. Open the `.html` files in a browser.

| Report | Path |
|---|---|
| JaCoCo (initial) | `DOPO-SlotMachine/informes/jacoco-inicial/index.html` |
| JaCoCo (final) | `DOPO-SlotMachine/informes/jacoco-final/index.html` |
| PMD (initial) | `DOPO-SlotMachine/informes/pmd/pmd-inicial.html` |
| PMD (final) | `DOPO-SlotMachine/informes/pmd/pmd-final.html` |

- **JaCoCo** (dynamic analysis) measures the test coverage of the `domain` package.
- **PMD** (static analysis) checks the code quality of the `domain` and `presentation` packages.

## 📝 Cycle 5 documents

The cycle 5 documents are in the **`docs/`** folder, at the root of the repository:

- The **reports document**, which explains the changes made to increase the JaCoCo coverage and to fix the PMD violations.
- The **cycle 5 retrospective**, like the retrospectives of the previous cycles.

## ⚙️ Maven (`pom.xml`)

`DOPO-SlotMachine/` already has its `pom.xml`, which contains:

- **Project structure:** `src` holds the source code (packages `domain`, `presentation` and `test`), the compiled `.class` files go to `bin` and the libraries are in `lib`.
- **Java version:** the code is compiled for Java 17.
- **Dependencies (tests only):** JUnit 5 (`junit-jupiter`), `junit-vintage-engine` to run JUnit 4 tests, and `junit-platform-launcher`, used by `AcceptanceRunner`.
- **Plugins:**
  - `maven-compiler-plugin`: compiles the `test` package only as tests.
  - `maven-dependency-plugin`: copies the JUnit jar into `lib/` (`mvn dependency:copy`).
  - `maven-surefire-plugin`: runs every `*Test` class, except `AcceptanceTest`, which is visual and is run from VS Code or with `lib/AcceptanceTest.jar`.
  - `jacoco-maven-plugin` (0.8.12): measures the coverage of the `domain` package.
  - `maven-pmd-plugin` (3.22.0): analyzes the code with the 7 Java rule categories (best practices, code style, design, documentation, error prone, multithreading and performance).

To generate the reports again (from `DOPO-SlotMachine/`, with **Java 21**):

```bash
mvn clean verify    # runs the tests and generates JaCoCo → target/site/jacoco/index.html
mvn pmd:pmd         # generates PMD → target/site/pmd.html
```

## ✅ Acceptance test

An acceptance test was added as a class, **`AcceptanceTest`**, in `DOPO-SlotMachine/src/test/`. It makes the machine visible and plays a full game step by step with every type of wheel and symbol, checking the state of the machine after every step.

The test was also packaged as a **JAR**, which is in the **`lib/`** folder: `DOPO-SlotMachine/lib/AcceptanceTest.jar`.

- To run it, double-click `AcceptanceTest.jar` (or run `java -jar DOPO-SlotMachine/lib/AcceptanceTest.jar`).
- The messages the machine shows during the game (the rebel wheel refusing to be locked and the jackpot) must be closed with **OK** to go on.
- At the end, a message tells whether the acceptance test passed or failed.
- The JAR is started by the `AcceptanceRunner` class (`DOPO-SlotMachine/src/test/`), which runs `AcceptanceTest` with JUnit.

## 📐 Design diagrams (Astah)

The Astah file is in the **`design/`** folder, at the root of the repository: `design/ClassDiagramSlotMachine1.asta`. It contains the class diagram and the sequence diagrams.

### Class diagram

1. Open `design/ClassDiagramSlotMachine1.asta` with **Astah**.
2. In the **Structure** tree (left panel), expand **ClassDiagramSlotMachine**. Inside it there are two folders, `classes` and `types`, and the **class diagram**.
3. Click the class diagram to open it.

### Sequence diagrams

Every method's sequence diagram is stored under its class, inside the `classes` folder:

1. Expand the `classes` folder.
2. Click a class (for example, `SlotMachine`) to expand it and list its methods.
3. Click a method: its sequence diagram is there.

```
ClassDiagramSlotMachine
├── classes
│   ├── SlotMachine
│   │   ├── addWheel(...)      → sequence diagram
│   │   ├── spin(...)          → sequence diagram
│   │   └── ...
│   └── ...
├── types
└── Class diagram
```
