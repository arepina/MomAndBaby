package com.repina.anastasia.momandbaby.Classes;

class Translator {

    static String translateWord(String word) {
        switch (word) {
            case "weight":
                return "Вес";
            case "height":
                return "Рост";
            case "howMuch":
                return "Оценка";
            case "temperature":
                return "Температура";
            case "length":
                return "Длительность";
            case "pills":
                return "Таблетки";
            case "note":
                return "Заметка";
            case "symptomes":
                return "Симптомы";
            case "vaccinationName":
                return "Название прививки";
            case "date":
                return "дата";
            default:
                return word;
        }
    }

}
