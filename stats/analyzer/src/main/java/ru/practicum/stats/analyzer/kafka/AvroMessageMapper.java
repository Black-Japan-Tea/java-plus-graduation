package ru.practicum.stats.analyzer.kafka;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class AvroMessageMapper {

    public <T extends SpecificRecordBase> T fromBytes(byte[] payload, Class<T> targetType) {
        try {
            DatumReader<T> reader = new SpecificDatumReader<>(targetType);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(payload, null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to deserialize Avro record", e);
        }
    }

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
