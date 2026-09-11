using InovacaoAguiaBranca.DTOs;
using InovacaoAguiaBranca.Models;
using InovacaoAguiaBranca.Repositories;
using MongoDB.Driver;

namespace InovacaoAguiaBranca.Services;

public class UsuarioService
{
    private readonly MongoDbContext _context;
    private readonly JwtService _jwtService;
    private static readonly List<Usuario> _inMemoryUsuarios = new();

    public UsuarioService(MongoDbContext context, JwtService jwtService)
    {
        _context = context;
        _jwtService = jwtService;
        InicializarUsuariosPadrao();
    }

    private void InicializarUsuariosPadrao()
    {
        if (_inMemoryUsuarios.Count == 0)
        {
            _inMemoryUsuarios.AddRange(new[]
            {
                new Usuario
                {
                    Id = "66db5e110000000000000001",
                    Nome = "Carlos Operador",
                    Email = "operador@aguiabranca.com.br",
                    SenhaHash = BCrypt.Net.BCrypt.HashPassword("123456"),
                    Cargo = "Motorista Instrutor",
                    Divisao = "Passageiros",
                    Perfil = "Operador"
                },
                new Usuario
                {
                    Id = "66db5e110000000000000002",
                    Nome = "Mariana Gestora",
                    Email = "gestor@aguiabranca.com.br",
                    SenhaHash = BCrypt.Net.BCrypt.HashPassword("123456"),
                    Cargo = "Coordenadora de Operações",
                    Divisao = "Logística",
                    Perfil = "Gestor"
                },
                new Usuario
                {
                    Id = "66db5e110000000000000003",
                    Nome = "Roberto Líder",
                    Email = "lider@aguiabranca.com.br",
                    SenhaHash = BCrypt.Net.BCrypt.HashPassword("123456"),
                    Cargo = "Diretor de Inovação e Frota",
                    Divisao = "Comércio",
                    Perfil = "Lider"
                }
            });
        }
    }

    public async Task<AuthResponse?> AutenticarAsync(LoginRequest request)
    {
        Usuario? usuario = null;
        var email = (request.Email ?? "").Trim().ToLowerInvariant();

        if (_context.IsConnected && _context.Usuarios != null)
        {
            try
            {
                usuario = await _context.Usuarios.Find(u => u.Email.ToLower() == email).FirstOrDefaultAsync();
            }
            catch { }
        }

        usuario ??= _inMemoryUsuarios.FirstOrDefault(u => u.Email.Equals(email, StringComparison.OrdinalIgnoreCase));

        if (usuario == null)
            return null;

        bool senhaValida = false;
        try
        {
            senhaValida = BCrypt.Net.BCrypt.Verify(request.Senha, usuario.SenhaHash);
        }
        catch
        {
            senhaValida = usuario.SenhaHash == request.Senha;
        }

        if (!senhaValida)
            return null;

        var token = _jwtService.GerarToken(usuario);

        return new AuthResponse
        {
            Token = token,
            UsuarioId = usuario.Id ?? Guid.NewGuid().ToString(),
            Nome = usuario.Nome,
            Email = usuario.Email,
            Perfil = usuario.Perfil,
            Cargo = usuario.Cargo,
            Divisao = usuario.Divisao,
            Expiracao = DateTime.UtcNow.AddHours(24)
        };
    }

    public async Task<AuthResponse> RegistrarAsync(RegisterRequest request)
    {
        var usuario = new Usuario
        {
            Id = MongoDB.Bson.ObjectId.GenerateNewId().ToString(),
            Nome = request.Nome,
            Email = request.Email,
            SenhaHash = BCrypt.Net.BCrypt.HashPassword(request.Senha),
            Cargo = request.Cargo,
            Divisao = request.Divisao,
            Perfil = request.Perfil,
            DataCriacao = DateTime.UtcNow
        };

        if (_context.IsConnected && _context.Usuarios != null)
        {
            try
            {
                await _context.Usuarios.InsertOneAsync(usuario);
            }
            catch { }
        }

        _inMemoryUsuarios.Add(usuario);

        var token = _jwtService.GerarToken(usuario);

        return new AuthResponse
        {
            Token = token,
            UsuarioId = usuario.Id,
            Nome = usuario.Nome,
            Email = usuario.Email,
            Perfil = usuario.Perfil,
            Cargo = usuario.Cargo,
            Divisao = usuario.Divisao,
            Expiracao = DateTime.UtcNow.AddHours(24)
        };
    }

    public async Task<List<Usuario>> ObterTodosAsync()
    {
        if (_context.IsConnected && _context.Usuarios != null)
        {
            try
            {
                var lista = await _context.Usuarios.Find(_ => true).ToListAsync();
                if (lista.Count > 0) return lista;
            }
            catch { }
        }
        return _inMemoryUsuarios;
    }
}
