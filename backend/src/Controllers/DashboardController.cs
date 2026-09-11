using InovacaoAguiaBranca.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace InovacaoAguiaBranca.Controllers;

[ApiController]
[Route("api/[controller]")]
public class DashboardController : ControllerBase
{
    private readonly DashboardService _dashboardService;

    public DashboardController(DashboardService dashboardService)
    {
        _dashboardService = dashboardService;
    }

    // Exclusivo da Liderança corporativa
    [Authorize(Roles = "Lider")]
    [HttpGet("resumo")]
    public async Task<IActionResult> ObterResumoExecutivo()
    {
        var resumo = await _dashboardService.ObterResumoExecutivoAsync();
        return Ok(resumo);
    }
}
