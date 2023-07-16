package pers.shawxingkwok.androidutil.view

import org.junit.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.functions

class ExtendsTest {
    @Test
    fun foo(){
        println(listOf(1,2,3, 1) - 1)
    }
}

abstract class A{
    init {
        val classes = mutableListOf<KClass<*>>()

        var clazz: KClass<*> = this::class
        while (clazz != A::class){
            println(clazz)
            classes += clazz
            clazz = clazz.java.superclass!!.kotlin
        }

        classes.flatMap { it.declaredFunctions.toList() }.map { it.name }.let(::println)
    }

    private fun foo(){}
}

abstract class B : A(){
    fun bar(){}
    private fun foo(){}
}

class C : B(){
    private fun foo(){}
}