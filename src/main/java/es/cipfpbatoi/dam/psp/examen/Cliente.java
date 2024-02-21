package es.cipfpbatoi.dam.psp.examen;

class Cliente implements Runnable {
    private final Barberia barberia;
    private final int id;

    public Cliente(Barberia barberia, int id) {
        this.barberia = barberia;
        this.id = id;
    }

    @Override
    public void run() {
        barberia.agregarClientes(this);
    }

    public int getId() {
        return id;
    }
}
