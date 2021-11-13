package com.example.mitiendapro.category;

import java.io.Serializable;

public class Category implements Serializable  {

        private String name;
        private int year;

        public Category(String name, int year) {
            this.name = name;
            this.year = year;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }


