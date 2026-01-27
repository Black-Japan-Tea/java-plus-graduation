package ru.practicum.stats.collector.kafka;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.specific.SpecificDatumWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class AvroMessageMapper {

    public byte[] toBytes(SpecificRecordBase record) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(record.getSchema());
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        try {
            writer.write(record, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize Avro record", e);
        }
    }
}
