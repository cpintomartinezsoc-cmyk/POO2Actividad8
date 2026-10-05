package cl.duoc.util;

import cl.duoc.interfaz.Identificable;

import javax.swing.JComboBox;

public final class Combos {

    private Combos() {
    }

    public static boolean seleccionarPorId(JComboBox<?> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Identificable entidad && entidad.getId() == id) {
                combo.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    public static Integer idSeleccionado(JComboBox<?> combo) {
        Object item = combo.getSelectedItem();
        if (item instanceof Identificable entidad) {
            return entidad.getId();
        }
        return null;
    }
}