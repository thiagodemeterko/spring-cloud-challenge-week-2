package br.com.caelum.eats.pagamento;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pagamentos")
@AllArgsConstructor
class PagamentoController {

	private PagamentoRepository pagamentoRepo;
	private PagamentoService pagamentoService;

	@GetMapping
	ResponseEntity<List<PagamentoDto>> lista() {
		return ResponseEntity.ok(pagamentoService.lista()
				.stream()
				.map(PagamentoDto::new)
				.collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	PagamentoDto detalha(@PathVariable("id") Long id) {
		return pagamentoRepo.findById(id)
				.map(PagamentoDto::new)
				.orElseThrow(ResourceNotFoundException::new);
	}

	@PostMapping
	ResponseEntity<PagamentoDto> cria(@RequestBody Pagamento pagamento, UriComponentsBuilder uriBuilder) {
		Pagamento salvo = pagamentoService.cria(pagamento);
		URI path = uriBuilder.path("/pagamentos/{id}").buildAndExpand(salvo.getId()).toUri();
		return ResponseEntity.created(path).body(new PagamentoDto(salvo));
	}

	@PutMapping("/{id}")
	PagamentoDto confirma(@PathVariable("id") Long id) {
		Pagamento pagamento = pagamentoService.confirma(id, new MudancaDeStatusDoPedido("pago"));
		return new PagamentoDto(pagamento);
	}

	@DeleteMapping("/{id}")
	PagamentoDto cancela(@PathVariable("id") Long id) {
		Pagamento pagamento = pagamentoRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
		pagamento.setStatus(Pagamento.Status.CANCELADO);
		pagamentoRepo.save(pagamento);
		return new PagamentoDto(pagamento);
	}

}