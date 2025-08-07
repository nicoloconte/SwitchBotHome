package it.myhouse.switchbot.model;

import java.util.List;

/**
 * Rappresenta una lista di dispositivi.
 * <p>
 * Questa classe viene utilizzata per incapsulare una collezione di oggetti {@link Device}.
 * Fornisce metodi getter e setter per accedere e modificare la lista dei dispositivi.
 * </p>
 *
 * @author nicoloconte
 * @since 1.0
 */
public class DeviceList {

    private List<Device> deviceList;

    /**
     * Restituisce la lista dei dispositivi.
     *
     * @return la lista di oggetti {@link Device}
     */
    public List<Device> getDeviceList() {
        return deviceList;
    }

    /**
     * Imposta la lista dei dispositivi.
     *
     * @param deviceList la nuova lista di oggetti {@link Device}
     */
    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    /**
     * Restituisce una rappresentazione in formato stringa della lista dei dispositivi.
     *
     * @return una stringa che rappresenta l'oggetto DeviceList
     */
    @Override
    public String toString() {
        return "DeviceList{deviceList=" + deviceList + '}';
    }
}