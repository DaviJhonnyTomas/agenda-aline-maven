/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package agendaalineweb.controllers;

import agendaalineweb.entities.Agendamento;
import agendaalineweb.entities.Cliente;
import agendaalineweb.entities.Procedimento;
import agendaalineweb.entities.Usuario;
import agendaalineweb.models.AgendamentoModel;
import agendaalineweb.models.ClienteModel;
import agendaalineweb.models.DataModel;
import agendaalineweb.models.ProcedimentoModel;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Utilizador
 */
@WebServlet(name = "CadastrarAgendamento", urlPatterns = {"/cadastrar-agendamento"})
public class CadastrarAgendamento extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuarioLogado");

        if (usuario != null) {
            AgendamentoModel agendamentoModel = new AgendamentoModel();
            ArrayList<Agendamento> agendamentos = agendamentoModel.selectAll(usuario.getId());
            ProcedimentoModel modelProcedimento = new ProcedimentoModel();
            ClienteModel modelCliente = new ClienteModel();
            String caminhoContexto = request.getContextPath();
            request.setAttribute("caminhoContexto", caminhoContexto);
            request.setAttribute("agendamentos", agendamentos);
            ArrayList<Procedimento> procedimentos = modelProcedimento.selectAll(usuario.getId());
            request.setAttribute("procedimentos", procedimentos);
            ArrayList<Cliente> clientes = modelCliente.selectAll(usuario.getIdNegocio());
            request.setAttribute("clientes", clientes);
            request.getRequestDispatcher("WEB-INF/pageAgendamentos.jsp").forward(request, response);
        } else {
            response.sendRedirect("login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        HttpSession sessao = request.getSession();
        Usuario usuario = (Usuario) sessao.getAttribute("usuarioLogado");
        String mensagem = null;
        ProcedimentoModel procedimentoModel = new ProcedimentoModel();

        if (usuario == null) {
            mensagem = "Você deve fazer login para realizar esta operação.";
        } else {
            String idCliente = request.getParameter("idCliente");
            boolean idClienteInformado = false;
            if (!idCliente.isEmpty()) { // se o id do cliente não for vazio (! serve para negar)
                idClienteInformado = true;
            }
            ArrayList<Procedimento> procedimentos2 = procedimentoModel.selectAll(usuario.getId());

            ArrayList<Integer> idsProcedimentos = new ArrayList();
            for (int i = 0; i < procedimentos2.size(); i++) {
                String idProcedimento = request.getParameter("idProcedimento" + i);
                if (idProcedimento != null) {
                    idsProcedimentos.add(Integer.parseInt(idProcedimento));
                }
            }
            boolean idsProcedimentosInformados = false;
            if (idsProcedimentos.size() > 0) {
                idsProcedimentosInformados = true;
            }

            String hora = request.getParameter("hora");

            LocalTime horaConvertida = null;
            boolean horaInformada = false;
            if (!hora.isEmpty()) {
                horaConvertida = LocalTime.parse(hora);
                horaInformada = true;
            }

            String data = request.getParameter("data");

            DataModel dataModel = new DataModel();
            LocalDate dataConvertida = null;

            boolean dataInformada = false;
            if (!data.isEmpty()) {
                dataConvertida = LocalDate.parse(data);
                dataInformada = true;
            }

            boolean dataAnterior = true;

            LocalDate dataHoje = LocalDate.now();

            if (idClienteInformado && idsProcedimentosInformados && horaInformada && dataInformada) {

                if (dataConvertida.isAfter(dataHoje) || dataConvertida.isEqual(dataHoje)) {
                    Agendamento agendamento = new Agendamento(horaConvertida, dataConvertida, Integer.parseInt(idCliente), usuario.getId());
                    AgendamentoModel agendamentoModel = new AgendamentoModel();
                    dataAnterior = false;
                    try {
                        agendamentoModel.insert(agendamento, idsProcedimentos);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                } else {
                    mensagem = "A data informada é anterior a data de hoje.";
                }

            } else {
                mensagem = "Todos os campos devem ser preenchidos.";

            }
        }
        request.setAttribute("mensagemErro", mensagem);
        AgendamentoModel agendamentoModel = new AgendamentoModel();
        ArrayList<Agendamento> agendamentos = agendamentoModel.selectAll(usuario.getId());
        request.setAttribute("agendamentos", agendamentos);
        ClienteModel clienteModel = new ClienteModel();
        ArrayList<Cliente> clientes = clienteModel.selectAll(usuario.getIdNegocio());
        request.setAttribute("clientes", clientes);
        ArrayList<Procedimento> procedimentos = procedimentoModel.selectAll(usuario.getId());
        request.setAttribute("procedimentos", procedimentos);

        String caminhoContexto = request.getContextPath();
        request.setAttribute("caminhoContexto", caminhoContexto);

        request.getRequestDispatcher("WEB-INF/pageAgendamentos.jsp").forward(request, response);

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
