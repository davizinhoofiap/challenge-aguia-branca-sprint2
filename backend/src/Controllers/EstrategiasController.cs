using System.Security.Claims;
using InovacaoAguiaBranca.DTOs;
using InovacaoAguiaBranca.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace InovacaoAguiaBranca.Controllers;

[ApiController]
[Route("api/[controller]")]
public class EstrategiasController : ControllerBase
{
    private readonly EstrategiaService _estrategiaService;

    public EstrategiasController(EstrategiaService estrategiaService)
    {
        _estrategiaService = estrategiaService;
    }

    // Todos os perfis autenticados podem consultar as orientações estratégicas
    [Authorize]
    [HttpGet]
    public async Task<IActionResult> Listar([FromQuery] bool apenasAtivas = false)
    {
        var lista = await _estrategiaService.ObterTodasAsync(apenasAtivas);
        return Ok(lista);
    }

    [Authorize]
    [HttpGet("{id}")]
    public async Task<IActionResult> ObterPorId(string id)
    {
        var estrategia = await _estrategiaService.ObterPorIdAsync(id);
        if (estrategia == null) return NotFound(new { mensagem = "Estratégia não encontrada." });
        return Ok(estrategia);
    }

    // Exclusivo da Liderança: Criar orientações estratégicas (CRUD)
    [Authorize(Roles = "Lider")]
    [HttpPost]
    public async Task<IActionResult> Criar([FromBody] CriarEstrategiaRequest request)
    {
        var autor = User.FindFirst(ClaimTypes.Name)?.Value ?? "Liderança Águia Branca";
        var criada = await _estrategiaService.CriarAsync(request, autor);
        return CreatedAtAction(nameof(ObterPorId), new { id = criada.Id }, criada);
    }

    // Exclusivo da Liderança: Atualizar orientações estratégicas
    [Authorize(Roles = "Lider")]
    [HttpPut("{id}")]
    public async Task<IActionResult> Atualizar(string id, [FromBody] AtualizarEstrategiaRequest request)
    {
        var atualizada = await _estrategiaService.AtualizarAsync(id, request);
        if (atualizada == null) return NotFound(new { mensagem = "Estratégia não encontrada." });
        return Ok(atualizada);
    }

    // Exclusivo da Liderança: Excluir/Arquivar
    [Authorize(Roles = "Lider")]
    [HttpDelete("{id}")]
    public async Task<IActionResult> Deletar(string id)
    {
        var sucesso = await _estrategiaService.DeletarAsync(id);
        if (!sucesso) return NotFound(new { mensagem = "Estratégia não encontrada." });
        return NoContent();
    }
}
