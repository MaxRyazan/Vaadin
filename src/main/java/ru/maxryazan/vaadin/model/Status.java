package ru.maxryazan.vaadin.model;

public enum Status {
    ACTIVE {
        @Override
        public String toString() {
            return "АКТИВЕН";
        }
    },
    CLOSED {
        @Override
        public String toString() {
            return "ЗАКРЫТ";
        }
    }
}