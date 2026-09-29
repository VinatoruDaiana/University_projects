package bll;

import dao.ClientDAO;
import model.Client;


import java.util.List;
import java.util.NoSuchElementException;

public class ClientBLL {

    private static ClientDAO clientDAO = new ClientDAO();


    public static Client findClientById(int id) {

        Client c = clientDAO.findById(id);
        if (c == null) {
            throw new NoSuchElementException("The client with id =" + id + " was not found!");
        }
        return c;
    }

    public static int insertClient(Client client) {
        return clientDAO.insert(client);
    }

    public static int deleteClient(int id) {
        return clientDAO.delete(id);
    }

    public static int updateClient(Client client) {
        return clientDAO.update(client);
    }

    public static List<Client> findAllClients() {
        return clientDAO.findAll();
    }

    public static void printAllClients(List<Client> clientList) {
        for (Client c : clientList)
            System.out.println(c.toString() + "\n");
    }
}
