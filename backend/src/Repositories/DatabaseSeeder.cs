using InovacaoAguiaBranca.Models;
using MongoDB.Driver;

namespace InovacaoAguiaBranca.Repositories;

public class DatabaseSeeder
{
    private readonly MongoDbContext _context;

    public DatabaseSeeder(MongoDbContext context)
    {
        _context = context;
    }

    public async Task SeedAsync()
    {
        if (!_context.IsConnected)
        {
            Console.WriteLine("[MongoDB Info] Operando em modo memória local tolerante a falhas (MongoDB Atlas não configurado).");
            return;
        }

        Console.WriteLine("[MongoDB Atlas] Conexao estabelecida com sucesso com o cluster MongoDB Atlas.");

        try
        {
            // 1. Seed de Usuários no Atlas (Perfis Corporativos Oficiais do Desafio Águia Branca)
            if (_context.Usuarios != null)
            {
                // Remove registros não corporativos ou legados se existirem
                await _context.Usuarios.DeleteManyAsync(u => 
                    u.Nome == "VictorH" || 
                    u.Nome == "FernandoP" || 
                    u.Nome == "Davi Silva" || 
                    u.Nome == "Professor FIAP" ||
                    u.Email == "victorh@aguiabranca.com.br" || 
                    u.Email == "fernandop@aguiabranca.com.br" || 
                    u.Email == "davi@aguiabranca.com.br" ||
                    u.Email == "professor@fiap.com.br"
                );

                var usuariosDesejados = new List<Usuario>
                {
                    new()
                    {
                        Id = "66db5e110000000000000001",
                        Nome = "Carlos Operador",
                        Email = "operador@aguiabranca.com.br",
                        SenhaHash = BCrypt.Net.BCrypt.HashPassword("123456"),
                        Cargo = "Motorista Instrutor",
                        Divisao = "Passageiros",
                        Perfil = "Operador",
                        DataCriacao = DateTime.UtcNow
                    },
                    new()
                    {
                        Id = "66db5e110000000000000002",
                        Nome = "Mariana Gestora",
                        Email = "gestor@aguiabranca.com.br",
                        SenhaHash = BCrypt.Net.BCrypt.HashPassword("123456"),
                        Cargo = "Coordenadora de Operações",
                        Divisao = "Logística",
                        Perfil = "Gestor",
                        DataCriacao = DateTime.UtcNow
                    },
                    new()
                    {
                        Id = "66db5e110000000000000003",
                        Nome = "Roberto Líder",
                        Email = "lider@aguiabranca.com.br",
                        SenhaHash = BCrypt.Net.BCrypt.HashPassword("123456"),
                        Cargo = "Diretor de Inovação e Frota",
                        Divisao = "Comércio",
                        Perfil = "Lider",
                        DataCriacao = DateTime.UtcNow
                    }
                };

                foreach (var usuario in usuariosDesejados)
                {
                    var existe = await _context.Usuarios.Find(u => 
                        u.Email.ToLower() == usuario.Email.ToLower()
                    ).AnyAsync();

                    if (!existe)
                    {
                        await _context.Usuarios.InsertOneAsync(usuario);
                    }
                }
                Console.WriteLine("[MongoDB Atlas] Colecao 'Usuarios' corporativos sincronizada.");
            }

            // 2. Seed de Estratégias no Atlas
            if (_context.Estrategias != null && await _context.Estrategias.CountDocumentsAsync(_ => true) == 0)
            {
                var estrategias = new List<Estrategia>
                {
                    new()
                    {
                        Id = "66db5e220000000000000001",
                        Titulo = "Eficiência Energética e Descarbonização da Frota",
                        Descricao = "Otimizar o consumo de combustível e transição para energias limpas nos trajetos intermunicipais.",
                        Categoria = "Sustentabilidade",
                        Campanha = "Inova GAB 2026",
                        Pilar = "Operação Sustentável",
                        Prioridade = 1,
                        Ativa = true,
                        DataCriacao = DateTime.UtcNow.AddDays(-30),
                        CriadoPor = "Diretoria de Operações"
                    },
                    new()
                    {
                        Id = "66db5e220000000000000002",
                        Titulo = "Experiência Digital do Passageiro",
                        Descricao = "Aprimorar a jornada do cliente desde a compra do bilhete até o pós-viagem nos serviços de passageiros.",
                        Categoria = "Experiência do Cliente",
                        Campanha = "Viagem do Futuro",
                        Pilar = "Excelência ao Cliente",
                        Prioridade = 2,
                        Ativa = true,
                        DataCriacao = DateTime.UtcNow.AddDays(-15),
                        CriadoPor = "Gerência de Inovação"
                    },
                    new()
                    {
                        Id = "66db5e220000000000000003",
                        Titulo = "Segurança Operacional e Zero Acidentes",
                        Descricao = "Implementação de tecnologias de telemetria e suporte ao motorista para máxima segurança viária.",
                        Categoria = "Segurança",
                        Campanha = "Segurança em Primeiro Lugar",
                        Pilar = "Respeito às Pessoas",
                        Prioridade = 1,
                        Ativa = true,
                        DataCriacao = DateTime.UtcNow.AddDays(-5),
                        CriadoPor = "Comitê de Segurança"
                    }
                };
                await _context.Estrategias.InsertManyAsync(estrategias);
                Console.WriteLine("[MongoDB Atlas] Colecao 'Estrategias' inicializada com sucesso.");
            }

            // 3. Seed de Ideias no Atlas
            if (_context.Ideias != null && await _context.Ideias.CountDocumentsAsync(_ => true) == 0)
            {
                var ideias = new List<Ideia>
                {
                    new()
                    {
                        Id = "66db5e330000000000000001",
                        Titulo = "Sensor de Calibragem Automática e Desgaste de Pneus",
                        Descricao = "Instalar sensores IoT nos eixos dos ônibus rodoviários para monitoramento contínuo da pressão e temperatura, reduzindo consumo de diesel.",
                        Categoria = "Eficiência e Manutenção",
                        AutorId = "66db5e110000000000000001",
                        AutorNome = "Carlos Operador",
                        Divisao = "Passageiros",
                        Status = "Aprovada",
                        EstrategiaId = "66db5e220000000000000001",
                        EstrategiaTitulo = "Eficiência Energética e Descarbonização da Frota",
                        ImpactoEstimado = "Alto",
                        Votos = 18,
                        Comentarios = 4,
                        DataCriacao = DateTime.UtcNow.AddDays(-10),
                        AiScore = 92,
                        AiPrioridade = "Alta",
                        AiAnalise = "Ideia com altíssimo retorno econômico direto em redução de consumo de diesel e segurança rodoviária.",
                        DataAvaliacaoIa = DateTime.UtcNow.AddDays(-10)
                    },
                    new()
                    {
                        Id = "66db5e330000000000000002",
                        Titulo = "Embarque por Reconhecimento Facial nas Rodoviárias",
                        Descricao = "Substituir a checagem manual de passagens por totens biométricos de embarque rápido nas plataformas de Vitória e Rio de Janeiro.",
                        Categoria = "Experiência do Cliente",
                        AutorId = "66db5e110000000000000001",
                        AutorNome = "Carlos Operador",
                        Divisao = "Passageiros",
                        Status = "Capturada",
                        EstrategiaId = "66db5e220000000000000002",
                        EstrategiaTitulo = "Experiência Digital do Passageiro",
                        ImpactoEstimado = "Médio",
                        Votos = 11,
                        Comentarios = 2,
                        DataCriacao = DateTime.UtcNow.AddDays(-3),
                        AiScore = 84,
                        AiPrioridade = "Alta",
                        AiAnalise = "Agiliza o fluxo de embarque e reduz filas nas rodoviárias centrais.",
                        DataAvaliacaoIa = DateTime.UtcNow.AddDays(-3)
                    }
                };
                await _context.Ideias.InsertManyAsync(ideias);
                Console.WriteLine("[MongoDB Atlas] Colecao 'Ideias' inicializada com sucesso.");
            }

            // 4. Seed de Projetos no Atlas
            if (_context.Projetos != null && await _context.Projetos.CountDocumentsAsync(_ => true) == 0)
            {
                var projetos = new List<Projeto>
                {
                    new()
                    {
                        Id = "66db5e440000000000000001",
                        Titulo = "Piloto de Telemetria e Pressão de Pneus em Tempo Real",
                        Descricao = "Instalação do sistema em 40 ônibus da linha Vitória x Belo Horizonte para validação dos ganhos de combustível.",
                        IdeiaOrigemId = "66db5e330000000000000001",
                        EstrategiaId = "66db5e220000000000000001",
                        EstrategiaTitulo = "Eficiência Energética e Descarbonização da Frota",
                        Responsavel = "Mariana Gestora",
                        Equipe = new() { "Mariana Gestora", "Carlos Operador", "Eng. Marcos" },
                        Status = "EmAndamento",
                        Etapa = "Piloto",
                        Progresso = 65.0,
                        Investimento = 85000.0,
                        Roi = 280.0,
                        LucroObtido = 140000.0,
                        ReducaoCustos = 95000.0,
                        GanhoProdutividade = 18.5,
                        DataInicio = DateTime.UtcNow.AddMonths(-2),
                        DataPrevisao = DateTime.UtcNow.AddMonths(1)
                    }
                };
                await _context.Projetos.InsertManyAsync(projetos);
                Console.WriteLine("[MongoDB Atlas] Colecao 'Projetos' inicializada com sucesso.");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"[MongoDB Atlas Error] Erro ao inicializar documentos: {ex.Message}");
        }
    }
}
