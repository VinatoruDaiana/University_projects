# Gestionare Lant Parfumuri

University project for managing a chain of perfume stores, implemented in two architectural variants: **MVC (Model-View-Controller)** and **MVP (Model-View-Presenter)**.

## Description

The application manages perfume stores, perfumes and stock information. It includes functionality for viewing, searching and filtering perfumes, performing CRUD operations, exporting out-of-stock perfume lists and displaying statistics.

The same domain is implemented using two different presentation architectures in order to compare their structure and responsibilities.

## Main Features

- Manage perfume stores
- Manage perfumes
- Manage stock
- Search and filter perfumes
- View perfume and store lists
- Export out-of-stock perfume lists
- Display statistics
- Database persistence
- Multilingual resources in the MVC version

## Architectures

### MVC

The `MVC` implementation separates the application into:

- **Model** – domain entities and repositories
- **View** – graphical user interface
- **Controller** – coordinates user actions and application logic

Main packages include:

```text
Model/
Controller/
View/
```

The MVC version also contains DTOs, mappers, repositories and view-model classes.

### MVP

The `MVP` implementation separates the application into:

- **Model** – domain entities and repositories
- **View** – graphical user interface
- **Presenter** – mediates communication between Model and View

Main packages include:

```text
Model/
Presenter/
View/
Connection/
```

The Presenter layer contains classes such as:

- `ParfumPresenter`
- `ParfumeriePresenter`
- `StocPresenter`

## Technologies

- Java
- JavaFX / Java GUI
- MySQL
- JDBC
- Maven
- DTO and Repository patterns
- Observer pattern
- MVC architecture
- MVP architecture

## Project Structure

```text
GestionareLantParfumuri/
├── MVC/
│   ├── src/
│   ├── pom.xml
│   ├── diagrame_de_activitati/
│   ├── diagrame_de_secventa/
│   └── ...
│
├── MVP/
│   ├── src/
│   ├── diagrame_de_activitati/
│   └── ...
│
└── README.md
```

## Documentation

The repository also includes:

- class diagrams
- activity diagrams
- sequence diagrams
- database diagrams
- project documentation
- SQL/database resources

## Purpose

The project was developed to practice software architecture and design by implementing the same perfume-chain management application using both MVC and MVP, highlighting the differences between the two approaches.
