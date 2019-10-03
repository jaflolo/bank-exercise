/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.bank.accountservice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GeneralUtils {

    private GeneralUtils(){}
    private static Random random = new Random();

    public static String generateAccountNumber(){
        long time = System.nanoTime();
        double random = Math.random() * 100;
        long mix = (long)(time * random);
        return String.valueOf(mix).substring(0,Constants.MAX_DIGITS_ACCOUNT_NUMBER);
    }

    public static String formatDateTimeToString(LocalDateTime dateTime){
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        String formattedDate = "";
        if(dateTime != null){
            formattedDate = dateTime.format(formatter);
        }

        return formattedDate;
    }



}
