using System.Security.Claims;
using InovacaoAguiaBranca.DTOs;
using InovacaoAguiaBranca.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace InovacaoAguiaBranca.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly UsuarioService _usuarioService;

    public AuthController(UsuarioService usuarioService)
    {
        _usuarioService = usuarioService;
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.Email) || string.IsNullOrWhiteSpace(request.Senha))
            return BadRequest(new { mensagem = "E-mail e senha são obrigatórios." });

        var resposta = await _usuarioService.AutenticarAsync(request);
        if (resposta == null)
            return Unauthorized(new { mensagem = "Credenciais inválidas. Verifique seu e-mail e senha." });

        return Ok(resposta);
    }

    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.Email) || string.IsNullOrWhiteSpace(request.Senha) || string.IsNullOrWhiteSpace(request.Nome))
            return BadRequest(new { mensagem = "Nome, e-mail e senha são obrigatórios." });

        var resposta = await _usuarioService.RegistrarAsync(request);
        return CreatedAtAction(nameof(Login), resposta);
    }

    [Authorize]
    [HttpGet("me")]
    public IActionResult ObterUsuarioLogado()
    {
        var id = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        var nome = User.FindFirst(ClaimTypes.Name)?.Value;
        var email = User.FindFirst(ClaimTypes.Email)?.Value;
        var perfil = User.FindFirst(ClaimTypes.Role)?.Value;

        return Ok(new { id, nome, email, perfil });
    }

    [Authorize(Roles = "Gestor,Lider")]
    [HttpGet("usuarios")]
    public async Task<IActionResult> ListarUsuarios()
    {
        var usuarios = await _usuarioService.ObterTodosAsync();
        var resultado = usuarios.Select(u => new
        {
            u.Id,
            u.Nome,
            u.Email,
            u.Cargo,
            u.Divisao,
            u.Perfil
        });
        return Ok(resultado);
    }
}
