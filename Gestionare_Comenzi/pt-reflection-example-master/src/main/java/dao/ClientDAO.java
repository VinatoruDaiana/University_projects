package dao;

import model.Client;
import java.util.List;
import javax.swing.*;

public class ClientDAO extends AbstractDAO<Client> {


    public ClientDAO() {
        super();
    }

    @Override
    public Client findById(int id) {
        return super.findById(id);
    }

    @Override
    public List<Client> findAll() {
        return super.findAll();
    }

    @Override
    public int insert(Client client) {
        return super.insert(client);
    }

    @Override
    public int delete(int id_client) {
        return super.delete(id_client);
    }

    @Override
    public int update(Client client) {
        return super.update(client);
    }

    @Override
    public void generateTable(JTable table, List<Client> clients) {
        super.generateTable(table, clients);

    }

}











