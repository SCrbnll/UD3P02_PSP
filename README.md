# Actividad: El Problema del Barbero Dormilón en Java

* [1. Objetivo](#1-objetivo)
* [2. Contexto](#2-contexto)
* [3. Requisitos](#3-requisitos)
* [4. Tareas](#4-tareas)
* [5. Esquema para Trabajar](#5-esquema-para-trabajar)
* [6. Entrega](#6-entrega)
* [7. Consideraciones](#7-consideraciones)
* [8. Referencias](#8-referencias)

![El Barbero Durmiente](md_media/Barbero.webp)

## 1. Objetivo

Implementar una solución al clásico problema de concurrencia del barbero dormilón utilizando mecanismos de concurrencia en Java, como hilos ([`Thread`][Thread]), bloqueos reentrantes ([`ReentrantLock`][ReentrantLock]), variables de condición ([`Condition`][Condition]), y semáforos ([`Semaphore`][Semaphore]).

## 2. Contexto

En una barbería hay un barbero, una silla de barbero, y N (Para este ejemplo 5) sillas para los clientes esperar si el barbero está ocupado. Si no hay clientes, el barbero se sienta en la silla y se duerme. Cuando llega un cliente, este tiene que despertar al barbero si está dormido o, si el barbero está atendiendo a otro cliente, esperar en una de las sillas disponibles. Si todas las sillas están ocupadas, el cliente se va.

## 3. Requisitos

1. **Barbero y Clientes:** Deben ser representados por hilos separados.
2. **Sincronización:** Utiliza [`ReentrantLock`][ReentrantLock] y [`Condition`][Condition] para sincronizar el acceso a la silla del barbero y la espera de los clientes.
3. **Entrada Aleatoria:** Los clientes deben llegar en momentos aleatorios, por ejemplo entre 5 y 10 segundos.
4. **Tiempo de Corte Aleatorio:** El barbero toma un tiempo aleatorio para cortar el cabello a cada cliente. Entre 10 y 15 segundos.
5. **Cola de Espera:** Implementa una cola para gestionar los clientes que esperan. Esta cola en una primera aproximación puede realizarse como lo que hemos visto en clase de utilizar una [`LinkedList`][LinkedList] por ejemplo, pero luego veremos las colas ([`Queue`][Queue]) que nos servirán precisamente para este tipo de propósitos.

## 4. Puntos Clave para la Reflexión

### **4.1. Decisión del Cliente:** Cuando un cliente llega a la barbería, su decisión de esperar o marcharse puede depender de diversos factores personales, como el tiempo estimado de espera para conseguir una silla vacía. En caso de que el cliente observe que todas las sillas están ocupadas, optará por irse. Este proceso se encuentra implementado en la función agregarClientes(). Esta "elección forzada" se realiza en el código verificando la disponibilidad de sillas.
### **4.2. Manejo de la Cola de Espera:** 
Una estructura de datos que representa la fila de espera es una cola (Queue) implementada con una lista enlazada (LinkedList). Utilizar una cola garantiza que los clientes sean atendidos en el orden en que llegaron a la barbería, ya que el método element() obtiene el primer cliente de la cola, quien llegó primero, evitando así que se salte a alguien en la fila y manteniendo contentos a nuestros clientes.
### **4.3. Concurrencia y Sincronización:** 
En este escenario, he utilizado bloqueos (locks) y condiciones (conditions) para gestionar el acceso a la cola. Me aseguro de que el barbero no sea interrumpido utilizando await() con la condición barberoSiesta, de modo que mientras haya un cliente en la barbería, el barbero esté despierto. Lo contrario sería poco profesional por parte del barbero. El bloqueo en agregarCliente() garantiza que si varios clientes llegan simultáneamente, solo uno pueda ingresar y ocupar una silla disponible, y hasta que se libere el bloqueo (unlock), el siguiente cliente no pueda verificar si hay sillas disponibles. La segunda condición, clienteListo, se utiliza para que mientras el barbero esté atendiendo a otro cliente, el cliente asociado a esta condición permanezca "bloqueado" en espera. Cuando el barbero termina de atender al cliente, llama a signal() en clienteListo para indicarle al cliente que el barbero está listo para atenderlo, permitiendo así que el hilo del cliente continúe su ejecución (y se prepare para su corte de cabello).
### **4.4. Justicia y Eficiencia:** 
El balance entre justicia y eficiencia se logra adecuadamente al priorizar que los clientes sean atendidos en el orden en que llegaron, lo que minimiza el tiempo de espera de cada cliente. Además, el barbero es eficiente al comenzar a trabajar tan pronto como un cliente entra en la barbería. Mis decisiones de diseño impactan en este equilibrio. Por ejemplo, si no utilizara una cola (Queue), el barbero podría atender a clientes que llegaron más tarde antes que a aquellos que llevan esperando más tiempo, lo que podría generar molestias en estos últimos.

## 5. Referencias

* [Thread]
* [ReentrantLock]
* [Condition]
* [Queue]

[Thread]: https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html
[ReentrantLock]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantLock.html
[Condition]: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/Condition.html
[Queue]: https://docs.oracle.com/javase/8/docs/api/java/util/Queue.html
