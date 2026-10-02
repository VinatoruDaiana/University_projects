# Gestionare Lant Parfumuri

University project for managing a chain of perfume stores, implemented in three architectural variants: **MVP (Model-View-Presenter)**, **MVVM (Model-View-ViewModel)** and **MVC (Model-View-Controller)**.

## Description

The application manages perfume stores, perfumes and stock information. It includes functionality for viewing, searching and filtering data, performing CRUD operations, exporting information about out-of-stock perfumes and displaying statistics.

The same application domain is implemented using three different architectural patterns in order to compare how responsibilities are divided between the presentation layer, application logic and data model.

## Main Features

- Manage perfume stores
- Manage perfumes
- Manage stock
- Search and filter perfumes
- Perform CRUD operations
- Export out-of-stock perfume lists
- Display statistics
- Persist data in a relational database

## Architectures

### MVP - Model-View-Presenter

The `MVP` implementation separates the application into:

- **Model** – domain entities and repositories
- **View** – graphical user interface
- **Presenter** – handles presentation logic and mediates communication between Model and View

Main packages:

```text
Model/
Presenter/
View/
Connection/
```

The Presenter layer includes classes such as:

- `ParfumPresenter`
- `ParfumeriePresenter`
- `StocPresenter`

### MVVM - Model-View-ViewModel

The `MVVM` implementation separates the application into:

- **Model** – domain entities and repositories
- **View** – graphical user interface
- **ViewModel** – exposes data and commands used by the View

Main packages:

```text
Model/
View/
ViewModel/
Connection/
```

The ViewModel layer includes:

- `ParfumVM`
- `ParfumerieVM`
- `StocVM`

and command classes such as:

- `ParfumCommands`
- `ParfumerieCommands`
- `StocCommands`

This version illustrates how ViewModels and commands can be used to reduce direct coupling between the user interface and application logic.

### MVC - Model-View-Controller

The `MVC` implementation separates the application into:

- **Model** – domain entities, repositories and application data
- **View** – graphical user interface
- **Controller** – receives user actions and coordinates operations between View and Model

Main packages include:

```text
Model/
Controller/
View/
```

The MVC version also contains DTOs, mappers, repositories, statistics-related components and Observer-based classes.

## Technologies

- Java
- JavaFX / Java GUI
- MySQL
- JDBC
- Maven
- Repository pattern
- DTO / Mapper pattern
- Observer pattern
- Command pattern
- MVP
- MVVM
- MVC

## Project Structure

```text
GestionareLantParfumuri/
├── MVP/
│   ├── src/
│   └── ...
├── MVVM/
│   ├── src/
│   └── ...
├── MVC/
│   ├── src/
│   ├── pom.xml
│   └── ...
└── README.md
```

## Documentation

The project also contains documentation and diagrams for the different implementations, including:

- class diagrams
- activity diagrams
- sequence diagrams
- use-case diagrams
- database diagrams
- SQL resources
- project documentation

## Purpose

The project was developed to study and compare three software architectural patterns — **MVP, MVVM and MVC** — by implementing the same perfume-chain management domain using different approaches to presentation and application logic.
