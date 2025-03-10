namespace SimpleTextEditorServer;

public class ObjectRef<T>
{
    public T? Value { get; set; }
       
    public ObjectRef() { }
       
    public ObjectRef(T reference)
    {
        Value = reference;
    }
}