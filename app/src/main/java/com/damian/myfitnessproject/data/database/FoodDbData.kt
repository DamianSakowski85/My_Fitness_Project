package com.damian.myfitnessproject.data.database

import com.damian.myfitnessproject.data.database.entity.Food
import javax.inject.Inject

class FoodDbData @Inject constructor() {

    fun getFoodData(): ArrayList<Food> {
        val foods = ArrayList<Food>()

        val eggs = Food(
            "Eggs", "One medium egg weigh ca. 60 gm.",
            155,
            11.0,
            1.1,
            13.0
        )
        foods.add(eggs)

        val greekYogurt = Food(
            "Greek Yogurt", "dairy",
            179,
            10.0,
            3.5,
            5.5
        )
        foods.add(greekYogurt)

        val butter = Food(
            "Butter", "dairy",
            716,
            81.0,
            0.1,
            0.9
        )
        foods.add(butter)

        val coconutOil = Food(
            "Coconut oil", "fat",
            862,
            100.0,
            0.0,
            0.0
        )
        foods.add(coconutOil)

        val extraVirginOlive = Food(
            "Extra Virgin Olive", "fat",
            884,
            100.0,
            0.0,
            0.0
        )
        foods.add(extraVirginOlive)

        val potatoes = Food(
            "Potatoes", "tubers",
            76,
            0.1,
            17.0,
            2.0
        )
        foods.add(potatoes)

        val sweetPotatoes = Food(
            "Sweet potatoes", "tubers",
            85,
            0.1,
            20.0,
            1.6
        )
        foods.add(sweetPotatoes)

        val almond = Food(
            "Almonds", "nuts",
            619,
            53.0,
            5.7,
            24.0
        )
        foods.add(almond)

        val coconuts = Food(
            "Coconuts", "nuts",
            354,
            33.0,
            15.0,
            3.3
        )
        foods.add(coconuts)

        val macadamiaNuts = Food(
            "Macadamia nuts", "nuts",
            718,
            76.0,
            14.0,
            8.0
        )
        foods.add(macadamiaNuts)

        val walnuts = Food(
            "Walnuts", "nuts",
            654,
            65.0,
            14.0,
            15.0
        )
        foods.add(walnuts)

        val oatFlakes = Food(
            "Oat flakes", "grain",
            372,
            7.0,
            58.7,
            13.5
        )
        foods.add(oatFlakes)

        val wholeGrainPenne = Food(
            "Wholegrain Penne", "grain",
            343,
            1.9,
            65.0,
            13.0
        )
        foods.add(wholeGrainPenne)

        val brownRice = Food(
            "Brown rice", "grain",
            110,
            0.9,
            23.0,
            2.6
        )
        foods.add(brownRice)

        val basmatiRice = Food(
            "Basmati rice", "grain",
            349,
            0.6,
            77.1,
            8.1
        )
        foods.add(basmatiRice)

        val quinoa = Food(
            "Quinoa", "grain",
            334,
            5.0,
            58.5,
            14.8
        )
        foods.add(quinoa)

        val greenBeans = Food(
            "Green beans", "legumes",
            31,
            0.2,
            7.0,
            1.8
        )
        foods.add(greenBeans)

        val kidneyBeans = Food(
            "Kidney beans", "legumes",
            332,
            0.8,
            60.0,
            24.0
        )
        foods.add(kidneyBeans)

        val lentils = Food(
            "Lentils", "legumes",
            116,
            0.4,
            20.0,
            9.0
        )
        foods.add(lentils)

        val peanuts = Food(
            "Peanuts", "legumes",
            567,
            49.0,
            16.0,
            26.0
        )
        foods.add(peanuts)

        val apples = Food(
            "Apples", "fruits",
            50,
            0.4,
            10.1,
            0.4
        )
        foods.add(apples)

        val avocados = Food(
            "Avocados", "fruits",
            169,
            15.3,
            4.1,
            2.0
        )
        foods.add(avocados)

        val bananas = Food(
            "Bananas", "fruits",
            97,
            0.3,
            21.8,
            1.0
        )
        foods.add(bananas)

        val blueberries = Food(
            "Blueberries", "fruit",
            51,
            0.6,
            9.0,
            0.8
        )
        foods.add(blueberries)

        val oranges = Food(
            "Oranges", "fruit",
            47,
            0.2,
            9.4,
            0.9
        )
        foods.add(oranges)

        val strawberries = Food(
            "Strawberries", "fruits",
            33,
            0.4,
            5.8,
            0.7
        )
        foods.add(strawberries)

        val pears = Food(
            "Pears", "fruits",
            57,
            0.1,
            15.0,
            0.4
        )
        foods.add(pears)

        val leanBeef = Food(
            "Lean beef", "meat",
            152,
            7.3,
            0.0,
            21.5
        )
        foods.add(leanBeef)

        val chickenBreasts = Food(
            "Chicken breasts", "meat",
            98,
            3.0,
            0.0,
            21.5
        )
        foods.add(chickenBreasts)

        val lamb = Food(
            "Lamb", "meat",
            243,
            19.0,
            0.0,
            18.0
        )
        foods.add(lamb)

        val salmon = Food(
            "Salmon", "fish",
            201,
            13.6,
            0.0,
            19.9
        )
        foods.add(salmon)

        val sardinesInOlive = Food(
            "Sardines in Olive", "fish",
            210,
            13.6,
            0.0,
            22.0
        )
        foods.add(sardinesInOlive)

        val sardinesInTomato = Food(
            "Sardines in tomato sauce", "fish",
            190,
            11.9,
            1.6,
            19.0
        )
        foods.add(sardinesInTomato)

        val trout = Food(
            "Trout", "fish",
            140,
            1.4,
            0.0,
            20.0
        )
        foods.add(trout)

        val tuna = Food(
            "Tuna", "fish",
            131,
            1.3,
            0.0,
            28.0
        )
        foods.add(tuna)

        val chiaSeeds = Food(
            "Chia seeds", "seeds",
            486,
            31.0,
            42.0,
            17.0
        )
        foods.add(chiaSeeds)

        val almondButter = Food(
            "Almond butter", "fats",
            613,
            56.0,
            19.0,
            21.0
        )
        foods.add(almondButter)

        val blackBeans = Food(
            "Black beans", "legumes",
            340,
            1.4,
            62.0,
            22.0
        )
        foods.add(blackBeans)

        val cranberries = Food(
            "Cranberries", "fruit",
            46,
            0.13,
            12.2,
            0.4
        )
        foods.add(cranberries)

        val flaxSeeds = Food(
            "Flax seeds", "seeds",
            533,
            42.0,
            29.0,
            18.0
        )
        foods.add(flaxSeeds)

        val honey = Food(
            "Honey", "",
            304,
            0.0,
            82.0,
            0.3
        )
        foods.add(honey)

        val hummus = Food(
            "Hummus", "vegetables",
            166,
            10.0,
            14.0,
            8.0
        )
        foods.add(hummus)

        val jasmineRice = Food(
            "Jasmine rice", "grain",
            355,
            0.0,
            79.0,
            7.0
        )
        foods.add(jasmineRice)

        val mozzarella = Food(
            "Mozazarella", "chesse",
            280,
            17.0,
            3.1,
            28.0
        )
        foods.add(mozzarella)

        val turkeyMeat = Food(
            "Turkey", "meat",
            188,
            7.0,
            0.1,
            29.0
        )
        foods.add(turkeyMeat)

        val pecans = Food(
            "Pecans", "nuts",
            690,
            72.0,
            14.0,
            9.0
        )
        foods.add(pecans)

        val plums = Food(
            "Plums", "fruit",
            45,
            0.3,
            11.0,
            0.7
        )
        foods.add(plums)

        val pumpkin = Food(
            "Pumpkin", "vegetables",
            26,
            0.1,
            7.0,
            1.0
        )
        foods.add(pumpkin)

        val raisins = Food(
            "Raisins", "fruit",
            299,
            0.5,
            79.0,
            3.1
        )
        foods.add(raisins)

        val raspberries = Food(
            "Raspberries", "fruit",
            52,
            0.7,
            12.0,
            1.2
        )
        foods.add(raspberries)

        return foods
    }
}