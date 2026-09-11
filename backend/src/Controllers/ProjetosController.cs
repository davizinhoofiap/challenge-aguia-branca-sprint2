using InovacaoAguiaBranca.DTOs;
using InovacaoAguiaBranca.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace InovacaoAguiaBranca.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ProjetosController : ControllerBase
{
    private readonly ProjetoService _projetoService;

    public ProjetosController(ProjetoService projetoService)
    {
        _projetoService = projetoService;
    }

    // Gestores e Líderes podem consultar o andamento dos projetos
    [Authorize(Roles = "Gestor,Lider")]
    [HttpGet]
    public async Task<IActionResult> Listar([FromQuery] string? status, [FromQuery] string? estrategiaId)
    {
        var lista = await _projetoService.ObterTodosAsync(status, estrategiaId);
        return Ok(lista);
    }

    [Authorize(Roles = "Gestor,Lider")]
    [HttpGet("{id}")]
    public async Task<IActionResult> ObterPorId(string id)
    {
        var projeto = await _projetoService.ObterPorIdAsync(id);
        if (projeto == null) return NotFound(new { mensagem = "Projeto não encontrado." });
        return Ok(projeto);
    }

    // Gestores cadastram projetos/iniciativas
    [Authorize(Roles = "Gestor")]
    [HttpPost]
    public async Task<IActionResult> Criar([FromBody] CriarProjetoRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.Titulo))
            return BadRequest(new { mensagem = "Título do projeto é obrigatório." });

        var criado = await _projetoService.CriarAsync(request);
        return CreatedAtAction(nameof(ObterPorId), new { id = criado.Id }, criado);
    }

    // Gestores atualizam dados, progresso e resultados obtidos (CRUD)
    [Authorize(Roles = "Gestor")]
    [HttpPut("{id}")]
    public async Task<IActionResult> Atualizar(string id, [FromBody] AtualizarProjetoRequest request)
    {
        var atualizado = await _projetoService.AtualizarAsync(id, request);
        if (atualizado == null) return NotFound(new { mensagem = "Projeto não encontrado." });
        return Ok(atualizado);
    }

    // Gestores podem remover projetos
    [Authorize(Roles = "Gestor")]
    [HttpDelete("{id}")]
    public async Task<IActionResult> Deletar(string id)
    {
        var sucesso = await _projetoService.DeletarAsync(id);
        if (!sucesso) return NotFound(new { mensagem = "Projeto não encontrado." });
        return NoContent();
    }
}
