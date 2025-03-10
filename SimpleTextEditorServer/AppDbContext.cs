using Microsoft.EntityFrameworkCore;
using SimpleTextEditorServer.Models;

namespace SimpleTextEditorServer;

public class AppDbContext : DbContext
{
    public DbSet<User> Users { get; set; }
    public DbSet<File> Files { get; set; }
    
    public AppDbContext() {}
    
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    { }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        optionsBuilder.UseMySql(Program.ConnectionString, Program.ServerVersion);
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<User>(user =>
        {
            user.HasKey(x => x.Id);
            user.HasIndex(x => x.Id).IsUnique();
            user.HasIndex(x => x.Email).IsUnique();

            user
                .HasMany(x => x.CreatedFiles)
                .WithOne(f => f.CreatedBy)
                .HasForeignKey(f => f.UserId)
                .HasPrincipalKey(x => x.Id);
            
            user.Property(x => x.Id).IsRequired().HasColumnType("VARCHAR(36)");
            user.Property(x => x.Email).IsRequired().HasMaxLength(255);
            user.Property(x => x.Password).IsRequired().HasMaxLength(255);
            user.Property(x => x.AuthToken).IsRequired().HasMaxLength(255);
        });

        modelBuilder.Entity<File>(file =>
        {
            file.HasKey(x => x.Id);
            file.HasIndex(x => x.Id).IsUnique();
            file.HasIndex(x => x.Name);
            file.HasIndex(x => x.DateCreated).IsDescending();
            file.HasIndex(x => x.UserId);
            
            file.Property(x => x.Id).IsRequired().HasColumnType("VARCHAR(36)");
            file.Property(x => x.Name).IsRequired().HasMaxLength(255);
            file.Property(x => x.DateCreated).IsRequired();
            file.Property(x => x.DateModified).IsRequired();
            file.Property(x => x.UserId).IsRequired();
        });
    }
}