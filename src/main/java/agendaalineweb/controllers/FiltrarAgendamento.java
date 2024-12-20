/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package agendaalineweb.controllers;

import java.sql.Date;
import agendaalineweb.models.ClienteModel;
import agendaalineweb.entities.Cliente;
import agendaalineweb.entities.Agendamento;
import agendaalineweb.entities.Usuario;
import agendaalineweb.models.AgendamentoModel;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Utilizador
 */
@WebServlet(name = "FiltrarAgendamento", urlPatterns = {"/filtrar-agendamento"})
public class FiltrarAgendamento extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String nomeCliente = request.getParameter("cliente");
        String data = request.getParameter("data");
        SimpleDateFormat formatador = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dataFormatada = null;
        java.sql.Date dataFormatadaSql = null;
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuarioLogado");
        if (usuario != null) {

            try {
                if (!data.isEmpty()) {
                    dataFormatada = formatador.parse(data);
                    dataFormatadaSql = new java.sql.Date(dataFormatada.getTime());
                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            //1° caso: filtrar somente pelo nome da cliente
            //2° caso: filtrar somente pela data
            //3° caso: filtrar pelos dois
            //1°:
            ClienteModel clienteModel = new ClienteModel();
            ArrayList<Agendamento> agendamentos = null;
            AgendamentoModel agendamentoModel = new AgendamentoModel();
            if (!data.isEmpty() && !nomeCliente.isEmpty()) {
                //empty => vazio
                //filtrar somente pelos dois (nome da cliente e data)
                try {
                    agendamentos = agendamentoModel.selectByDataAndNome(dataFormatadaSql, nomeCliente, usuario.getId());
                } catch (SQLException ex) {
                    Logger.getLogger(FiltrarAgendamento.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (!data.isEmpty()) {
                //filtrar somente pela data
                agendamentos = agendamentoModel.selectByData(dataFormatadaSql, usuario.getId());
            } else if (!nomeCliente.isEmpty()) {
                //filtrar somente pelo nome da cliente
                System.out.println("======================================================= caiu aqui");
                ArrayList<Cliente> clientesEncontrados = clienteModel.selectByNome(nomeCliente);
                int[] idsClientes = new int[clientesEncontrados.size()];
                for (int i = 0; i < clientesEncontrados.size(); i++) {
                    idsClientes[i] = clientesEncontrados.get(i).getId();
                }

                agendamentos = agendamentoModel.selectAgendamentosByIdsClientes(idsClientes, usuario.getId());

            }
            request.setAttribute("agendamentos", agendamentos);
            String caminhoContexto = request.getContextPath();
            request.setAttribute("caminhoContexto", caminhoContexto);

            request.getRequestDispatcher("WEB-INF/pageAgendamentos.jsp").forward(request, response);
        } else {
            response.sendRedirect("login");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
