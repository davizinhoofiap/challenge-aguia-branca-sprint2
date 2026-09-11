using System.Security.Claims;
using InovacaoAguiaBranca.DTOs;
using InovacaoAguiaBranca.Models;
using InovacaoAguiaBranca.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace InovacaoAguiaBranca.Controllers;

[ApiController]
[Route("api/[controller]")]
public class IdeiasController : ControllerBase
{
    private readonly IdeiaService _ideiaService;
    private readonly UsuarioService _usuarioService;

    public IdeiasController(IdeiaService ideiaService, UsuarioService usuarioService)
    {
        _ideiaService = ideiaService;
        _usuarioService = usuarioService;
    }

    [Authorize]
    [HttpGet]
    public async Task<IActionResult> Listar([FromQuery] string? status, [FromQuery] string? estrategiaId)
    {
        var perfil = User.FindFirst(ClaimTypes.Role)?.Value;
        var usuarioId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

        // Se for Operador, lista apenas as próprias ideias cadastradas
        string? autorFiltro = (perfil == "Operador") ? usuarioId : null;

        var ideias = await _ideiaService.ObterTodasAsync(autorFiltro, status, estrategiaId);
        return Ok(ideias);
    }

    [Authorize]
    [HttpGet("{id}")]
    public async Task<IActionResult> ObterPorId(string id)
    {
        var ideia = await _ideiaService.ObterPorIdAsync(id);
        if (ideia == null) return NotFound(new { mensagem = "Ideia não encontrada." });
        return Ok(ideia);
    }

    // Operadores e Gestores podem cadastrar ideias (com IA calculando pontuação automaticamente)
    [Authorize]
    [HttpPost]
    public async Task<IActionResult> Criar([FromBody] CriarIdeiaRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.Titulo) || string.IsNullOrWhiteSpace(request.Descricao))
            return BadRequest(new { mensagem = "Título e descrição são obrigatórios." });

        var usuarioId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? Guid.NewGuid().ToString();
        var nome = User.FindFirst(ClaimTypes.Name)?.Value ?? "Colaborador Águia Branca";
        var divisao = User.FindFirst("divisao")?.Value ?? request.Divisao;

        var autor = new Usuario
        {
            Id = usuarioId,
            Nome = nome,
            Divisao = divisao
        };

        var criada = await _ideiaService.CriarAsync(request, autor);
        return CreatedAtAction(nameof(ObterPorId), new { id = criada.Id }, criada);
    }

    // Gestores e Líderes podem priorizar e alterar status da ideia (ex: Aprovada, Reprovada, EmAvaliacao)
    [Authorize(Roles = "Gestor,Lider")]
    [HttpPatch("{id}/status")]
    public async Task<IActionResult> AtualizarStatus(string id, [FromBody] AtualizarIdeiaStatusRequest request)
    {
        var atualizada = await _ideiaService.AtualizarStatusAsync(id, request.Status);
        if (atualizada == null) return NotFound(new { mensagem = "Ideia não encontrada." });
        return Ok(atualizada);
    }

    // Reavaliar ou disparar score da IA explicitamente
    [Authorize]
    [HttpPost("{id}/avaliar-ia")]
    public async Task<IActionResult> AvaliarComIa(string id)
    {
        var resultado = await _ideiaService.ReavaliarComIaAsync(id);
        if (resultado == null) return NotFound(new { mensagem = "Ideia não encontrada." });
        return Ok(resultado);
    }

    [Authorize]
    [HttpDelete("{id}")]
    public async Task<IActionResult> Deletar(string id)
    {
        var usuarioId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? string.Empty;
        var perfil = User.FindFirst(ClaimTypes.Role)?.Value ?? "Operador";

        var sucesso = await _ideiaService.DeletarAsync(id, usuarioId, perfil);
        if (!sucesso) return Forbid();

        return NoContent();
    }
}
