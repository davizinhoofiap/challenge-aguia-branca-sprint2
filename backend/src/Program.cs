using System.Text;
using InovacaoAguiaBranca.Configurations;
using InovacaoAguiaBranca.Repositories;
using InovacaoAguiaBranca.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;

var builder = WebApplication.CreateBuilder(args);

// Configurações fortemente tipadas
builder.Services.Configure<MongoDbSettings>(builder.Configuration.GetSection("MongoDbSettings"));
builder.Services.Configure<JwtSettings>(builder.Configuration.GetSection("JwtSettings"));
builder.Services.Configure<GeminiSettings>(builder.Configuration.GetSection("GeminiSettings"));

var jwtSecret = builder.Configuration["JwtSettings:Secret"] ?? "AguiaBrancaInovacaoSecretKey2026SuperSegura!";
var jwtIssuer = builder.Configuration["JwtSettings:Issuer"] ?? "InovacaoAguiaBranca";
var jwtAudience = builder.Configuration["JwtSettings:Audience"] ?? "InovacaoAguiaBrancaApp";

// Autenticação JWT
builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.RequireHttpsMetadata = false;
    options.SaveToken = true;
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(jwtSecret)),
        ValidateIssuer = true,
        ValidIssuer = jwtIssuer,
        ValidateAudience = true,
        ValidAudience = jwtAudience,
        ValidateLifetime = true,
        ClockSkew = TimeSpan.Zero
    };
});

builder.Services.AddAuthorization();

// Injeção de Dependências
builder.Services.AddSingleton<MongoDbContext>();
builder.Services.AddSingleton<DatabaseSeeder>();
builder.Services.AddSingleton<JwtService>();
builder.Services.AddHttpClient<GeminiAiService>();
builder.Services.AddScoped<UsuarioService>();
builder.Services.AddScoped<EstrategiaService>();
builder.Services.AddScoped<IdeiaService>();
builder.Services.AddScoped<ProjetoService>();
builder.Services.AddScoped<DashboardService>();

// CORS irrestrito para permitir conexão do app Android (emulador 10.0.2.2 ou Wi-Fi)
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

// Swagger com suporte a Bearer Token JWT
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "Plataforma de Inovação Corporativa - Grupo Águia Branca",
        Version = "v1",
        Description = "API REST oficial da Sprint 2 com controle de acesso (Roles: Operador, Gestor, Lider), NoSQL MongoDB Atlas e IA Gemini."
    });

    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Description = "Insira o token JWT neste formato: Bearer {seu_token}",
        Name = "Authorization",
        In = ParameterLocation.Header,
        Type = SecuritySchemeType.ApiKey,
        Scheme = "Bearer"
    });

    c.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            Array.Empty<string>()
        }
    });
});

var app = builder.Build();

app.UseCors("AllowAll");

// Swagger disponível em desenvolvimento e produção para facilitar a banca examinadora
app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "Inovação Águia Branca API v1");
    c.RoutePrefix = string.Empty; // Swagger abre diretamente na raiz: http://localhost:5000/
});

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

Console.WriteLine("===============================================================");
Console.WriteLine("Plataforma de Inovacao Grupo Aguia Branca - Backend .NET 8");
Console.WriteLine("Swagger disponivel em: http://localhost:5000/");
Console.WriteLine("Contas padrao para teste (Senha: 123456):");
Console.WriteLine("   - Operador: operador@aguiabranca.com.br");
Console.WriteLine("   - Gestor:   gestor@aguiabranca.com.br");
Console.WriteLine("   - Lider:    lider@aguiabranca.com.br");
Console.WriteLine("===============================================================");

var seeder = app.Services.GetRequiredService<DatabaseSeeder>();
await seeder.SeedAsync();

app.Run("http://0.0.0.0:5000");
