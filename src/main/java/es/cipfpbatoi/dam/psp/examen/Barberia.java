package es.cipfpbatoi.dam.psp.examen;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barberia {
    private final int espaciosDisponibles = 5;
    private final Queue<Cliente> asientos;
    private final Lock lock;
    private final Condition barberoDurmiendo;
    private final Condition clienteListo;

    public Barberia() {
        asientos = new LinkedList<>();
        lock = new ReentrantLock();
        barberoDurmiendo = lock.newCondition();
        clienteListo = lock.newCondition();
    }

    public void agregarClientes(Cliente cliente) {
        lock.lock();
        try {
            if (asientos.size() == espaciosDisponibles) {
                System.out.println("La barbería está completa, el cliente " + cliente.getId() + " se marcha decepcionado.");
                Thread.currentThread().interrupt();
                return;
            }
            asientos.add(cliente);
            System.out.println("El cliente " + cliente.getId() + " acude a la barbería y se sienta en una de las sillas disponibles.");
            barberoDurmiendo.signal();
            clienteListo.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void cortarBarba() throws InterruptedException {
        lock.lock();
        try {
            while (asientos.isEmpty()) {
                System.out.println("El barbero se duerme en una silla.");
                barberoDurmiendo.await();
            }

            Cliente cliente = asientos.element();
            clienteListo.signal();
            System.out.println("El barbero le recorta la barba al cliente " + cliente.getId());
            lock.unlock();
            Thread.sleep(new Random().nextInt(4000) + 6000);
            lock.lock();
            asientos.poll();
            System.out.println("El barbero ha terminado el servicio con el cliente " + cliente.getId());
        } finally {
            lock.unlock();
        }
    }

    public void abrirBarberia() {
        Thread hiloBarbero = new Thread(new Barbero(this));
        hiloBarbero.start();

        Thread[] clientes = new Thread[10];

        for (int i = 0; i < 10; i++) {
            Cliente cliente = new Cliente(this, i + 1);
            try {
                Thread.sleep(new Random().nextInt(1000) + 2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            clientes[i] = new Thread(cliente);
            clientes[i].start();
        }

        for (Thread t : clientes) {
            if (t != null) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (estaVacia()) {
            System.out.println("Es la hora de cerrar la barbería");
            hiloBarbero.interrupt();
        }
    }

    public boolean estaVacia() {
        return asientos.isEmpty();
    }
}
