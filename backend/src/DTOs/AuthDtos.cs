namespace InovacaoAguiaBranca.DTOs;

public class LoginRequest
{
    public string Email { get; set; } = string.Empty;
    public string Senha { get; set; } = string.Empty;
}

public class RegisterRequest
{
    public string Nome { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Senha { get; set; } = string.Empty;
    public string Cargo { get; set; } = string.Empty;
    public string Divisao { get; set; } = "Passageiros";
    public string Perfil { get; set; } = "Operador"; // Operador, Gestor, Lider
}

public class AuthResponse
{
    public string Token { get; set; } = string.Empty;
    public string UsuarioId { get; set; } = string.Empty;
    public string Nome { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Perfil { get; set; } = string.Empty;
    public string Cargo { get; set; } = string.Empty;
    public string Divisao { get; set; } = string.Empty;
    public DateTime Expiracao { get; set; }
}
