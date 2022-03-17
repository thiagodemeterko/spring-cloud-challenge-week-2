package br.com.caelum.eats.pagamento;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PagamentoService {

    private PedidoClienteComFeign pedidoCliente;
    private PagamentoRepository pagamentoRepo;

    @HystrixCommand(threadPoolKey = "listaThreadPool")
    public List<Pagamento> lista() {
        return pagamentoRepo.findAll();
    }

    @HystrixCommand(threadPoolKey = "criaThreadPool")
    public Pagamento cria(Pagamento pagamento) {
        pagamento.setStatus(Pagamento.Status.CRIADO);
        return pagamentoRepo.save(pagamento);bul
    }

    @HystrixCommand(fallbackMethod = "confirmaFallback", threadPoolKey = "confirmaThreadPool")
    public Pagamento confirma(Long id, MudancaDeStatusDoPedido body) {
        Pagamento pagamento = pagamentoRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
        pedidoCliente.notificaServicoDePedidoParaMudarStatus(pagamento.getPedidoId(), body);
        pagamento.setStatus(Pagamento.Status.CONFIRMADO);
        pagamentoRepo.save(pagamento);
        return pagamento;
    }

    private Pagamento confirmaFallback(Long id, MudancaDeStatusDoPedido body) {
        Pagamento pagamento = pagamentoRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
        pagamento.setStatus(Pagamento.Status.PROCESSANDO);
        pagamentoRepo.save(pagamento);
        return pagamento;
    }

}

