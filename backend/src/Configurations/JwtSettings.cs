namespace InovacaoAguiaBranca.Configurations;

public class JwtSettings
{
    public string Secret { get; set; } = "AguiaBrancaInovacaoSecretKey2026SuperSegura!";
    public string Issuer { get; set; } = "InovacaoAguiaBranca";
    public string Audience { get; set; } = "InovacaoAguiaBrancaApp";
    public int ExpirationHours { get; set; } = 24;
}
