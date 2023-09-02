package ru.aniby.felmonapi.database;

public interface DatabaseHolderInterface {
    int getId();
    void setId(int id);

    default int searchInDatabase() {
        return -1;
    }

    default int save() {
        return -1;
    }

    default void delete() {

    }
}
