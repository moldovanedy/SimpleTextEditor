using System;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SimpleTextEditorServer;

public static class FileManager
{
    public static bool CreateFile(Guid id, DateTimeOffset exactTimestamp)
    {
        try
        {
            string folderPath = Path.Combine(GetDataFolder(), $"{exactTimestamp:yyyy-MM-dd}");
            Directory.CreateDirectory(folderPath);
            File.Create(Path.Combine(folderPath, $"{id}")).Dispose();
            
            return true;
        }
        catch (Exception ex)
        {
            Trace.TraceError(ex.Message);
            return false;
        }
    }
    
    public static bool DeleteFile(Guid id, DateTimeOffset fileCreationTime)
    {
        try
        {
            string dirPath = Path.Combine(GetDataFolder(), $"{fileCreationTime:yyyy-MM-dd}");
            File.Delete(Path.Combine(dirPath, $"{id}"));

            //if the directory hasn't got any files left, delete the directory as well
            if (!Directory.EnumerateFiles(dirPath).Any())
            {
                Directory.Delete(dirPath, true);
            }
            return true;
        }
        catch (Exception ex)
        {
            Trace.TraceError(ex.Message);
            return false;
        }
    }

    public static async Task<string?> GetFileContentAsync(Guid id, DateTimeOffset fileCreationTime)
    {
        try
        {
            string filePath = Path.Combine(GetDataFolder(), $"{fileCreationTime:yyyy-MM-dd}", $"{id}");
            return Encoding.UTF8.GetString(await File.ReadAllBytesAsync(filePath));
        }
        catch (Exception ex)
        {
            Trace.TraceError(ex.Message);
            return null;
        }
    }
    
    public static async Task<bool> UpdateFileAsync(
        Guid id, 
        DateTimeOffset fileCreationTime, 
        string diff, 
        bool isAdded, 
        int index,
        ObjectRef<bool> isTooLarge)
    {
        isTooLarge.Value = false;
        
        if (string.IsNullOrEmpty(diff))
        {
            return true;
        }

        if (index < 0)
        {
            return false;
        }
        
        string filePath = Path.Combine(GetDataFolder(), $"{fileCreationTime:yyyy-MM-dd}", $"{id}");
        if (!File.Exists(filePath))
        {
            return false;
        }

        FileStream? fs = null;
        
        try
        {
            fs = File.Open(filePath, FileMode.OpenOrCreate, FileAccess.ReadWrite);
            if (index > fs.Length)
            {
                return false;
            }

            //500 KB is the maximum size of a file
            if (fs.Length > 500_000 && isAdded)
            {
                isTooLarge.Value = true;
                return false;
            }

            byte[] diffBytes = Encoding.UTF8.GetBytes(diff);
            if (isAdded)
            {
                fs.Seek(index, SeekOrigin.Begin);

                if (index == fs.Length)
                {
                    await fs.WriteAsync(diffBytes);
                }
                else
                {
                    byte[] remainingBytes = new byte[fs.Length - index];
                    await fs.ReadExactlyAsync(remainingBytes, 0, remainingBytes.Length);
                    
                    fs.Seek(index, SeekOrigin.Begin);
                    fs.Write(diffBytes);
                    await fs.WriteAsync(remainingBytes);
                }
            }
            else
            {
                int offset = index + diffBytes.Length;
                fs.Seek(offset, SeekOrigin.Begin);
                
                byte[] remainingBytes = new byte[fs.Length - offset];
                await fs.ReadExactlyAsync(remainingBytes, 0, remainingBytes.Length);

                fs.Seek(offset - diffBytes.Length, SeekOrigin.Begin);
                fs.SetLength(fs.Length - diffBytes.Length);
                await fs.WriteAsync(remainingBytes);
            }
            
            fs.Close();
            return true;
        }
        catch (Exception ex)
        {
            fs?.Close();
            Trace.TraceError(ex.Message);
            return false;
        }
    }
    
    public static async Task<bool> FullyUpdateFileAsync(
        Guid id, 
        DateTimeOffset fileCreationTime, 
        string content)
    {
        string filePath = Path.Combine(GetDataFolder(), $"{fileCreationTime:yyyy-MM-dd}", $"{id}");
        if (!File.Exists(filePath))
        {
            return false;
        }

        FileStream? fs = null;
        
        try
        {
            fs = File.Open(filePath, FileMode.OpenOrCreate, FileAccess.Write);
            byte[] diffBytes = Encoding.UTF8.GetBytes(content);
            await fs.WriteAsync(diffBytes);
            
            fs.Close();
            return true;
        }
        catch (Exception ex)
        {
            fs?.Close();
            Trace.TraceError(ex.Message);
            return false;
        }
    }

    public static int GetFileSize(Guid id, DateTimeOffset fileCreationTime)
    {
        string filePath = Path.Combine(GetDataFolder(), $"{fileCreationTime:yyyy-MM-dd}", $"{id}");
        if (!File.Exists(filePath))
        {
            return -1;
        }

        try
        {
            FileInfo info = new(filePath);
            return (int)info.Length;
        }
        catch (Exception ex)
        {
            Trace.TraceError(ex.Message);
            return -1;
        }
    }

    private static string GetDataFolder()
    {
        return Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
            "SimpleTextEditorServer");
    }
}