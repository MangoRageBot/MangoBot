/*
 * Copyright (c) 2023. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.mangobot.test;


import org.mangorage.mangobot.basicutils.LogHelper;

public class Test {


    public static void openMenu() {

    }

    public static void openMenu(String menuName) {

    }



    public static void main(String[] args) {

        var fileName = "test.txt";
        var ext = ".txt";
        var fileNameNoExt = fileName.substring(0, fileName.length() - ext.length());
        LogHelper.info(fileNameNoExt);

        int pointsPerDay = 200;
        int pointsPerDollar = 100 / 5;
        int days = 30;

        int potentialEarnedPoints = pointsPerDay * days;
        int earnedPoints = 529;

        int potentialTotalIncome = potentialEarnedPoints / pointsPerDollar;
        int totalIncome = earnedPoints / pointsPerDollar;

        LogHelper.info("Estimated Income: %s".formatted(potentialTotalIncome));
        LogHelper.info("Earned Income: %s".formatted(totalIncome));



    }

}
