package br.com.caelum.eats.pagamento;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PedidoService {

    private PedidoClienteComFeign pedidoCliente;
    private PagamentoRepository pagamentoRepo;

    @HystrixCommand(fallbackMethod = "notificaServicoDePedidoParaMudarStatusFallback")
    public Pagamento notificaServicoDePedidoParaMudarStatus(Long id, MudancaDeStatusDoPedido body) {
        Pagamento pagamento = pagamentoRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
        pedidoCliente.notificaServicoDePedidoParaMudarStatus(pagamento.getPedidoId(), body);
        pagamento.setStatus(Pagamento.Status.CONFIRMADO);
        pagamentoRepo.save(pagamento);
        return pagamento;
    }

    private Pagamento notificaServicoDePedidoParaMudarStatusFallback(Long id, MudancaDeStatusDoPedido body) {
        Pagamento pagamento = pagamentoRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
        pagamento.setStatus(Pagamento.Status.PROCESSANDO);
        pagamentoRepo.save(pagamento);
        return pagamento;
    }

}

