package com.repina.anastasia.momandbaby.Classes;


class Translator {

    static String translate(String line) {
        switch (line) {
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
            default:
                return line;
        }
    }
}
