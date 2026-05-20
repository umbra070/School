

package org.skyschool.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.LongStream;

@RestController
@RequestMapping("/info")
public class InfoController {
    @Value("${server.port}")
    private int port;

    private final Logger logger = LoggerFactory.getLogger(InfoController.class);

    //GET http://localhost:port(8080 default)/info/port
    @GetMapping("/port")
    public ResponseEntity<Integer> getPort(){
        return ResponseEntity.ok(port);
    }

    /**
     *         <p>
     *         long limit1 = 1000000L;
     *         long limit3 = 1000000000L; // Не воспроизводимо на Stream.iterate.parallel из-за OutOfMemory ошибки.
     *         long limit4 = 300000000L; // Не воспроизводимо на Stream.iterate.parallel из-за OutOfMemory ошибки.
     *         long limit5 = 250000000L; // Не воспроизводимо на Stream.iterate.parallel из-за OutOfMemory ошибки.
     *         long limit6 = 200000000L; // Не воспроизводимо на Stream.iterate.parallel из-за OutOfMemory ошибки.
     *         long limit7 = 131999999L; // Верхняя граница после которой Stream.iterate.parallel сваливается в OutOfMemory
     *         </p>
     *         <p>
     *         limit1: result: 1784293664; run time by test InfoControllerTestWithDb: 515ms; clear run time: 15ms
     *         int someResult = Stream.iterate(1, a -> a +1) .limit(limit1) .reduce(0, (a, b) -> a + b );
     *         limit1: result: 1784293664; run time by test InfoControllerTestWithDb:552ms clear run time: 47ms
     *         int someResult = Stream.iterate(1, a -> a + 1).limit(limit1).parallel().reduce(0, (a, b) -> a + b);
     *         limit1: result: 1784293664; run time by test InfoControllerTestWithDb: 589ms; clear run time: 50ms
     *         int someResult = Stream.iterate(1, a -> a + 1).parallel().limit(limit1).reduce(0, (a, b) -> a + b);
     *         limit1: result: 1784293664 ; run time by test InfoControllerTestWithDb: 495ms; clear run time: 33ms
     *         int someResult = IntStream.rangeClosed(1, limit1).parallel().sum();
     *         limit1: result: 1784293664 ; run time by test InfoControllerTestWithDb: 568ms; clear run time: 13ms
     *         int someResult = IntStream.rangeClosed(1, limit1).sum();
     *         </p>
     *         **********************Experiments with limit2 **************************
     *         <p>
     *         limit2: result: 5000000050000000 ; run time by test InfoControllerTestWithDb: 1s 443ms; clear run time: 939ms
     *         long someResult = Stream.iterate(1L, a -> a +1L) .limit(limit2) .reduce(0L, (a, b) -> a + b );
     *         limit2: result: 5000000050000000 ; run time by test InfoControllerTestWithDb: 2s 913ms; clear run time: 2428ms
     *         long someResult = Stream.iterate(1L, a -> a + 1L).limit(limit2).parallel().reduce(0L, (a, b) -> a + b);
     *         limit2: result: 5000000050000000 ; run time by test InfoControllerTestWithDb: 2s 890ms; clear run time: 2386ms
     *         long someResult = Stream.iterate(1L, a -> a + 1L).parallel().limit(limit2).reduce(0L, (a, b) -> a + b);
     *         limit2: result: 5000000050000000 ; run time by test InfoControllerTestWithDb: 585ms; clear run time: 53ms
     *         long someResult = LongStream.rangeClosed(1L, limit2).parallel().sum();
     *         limit2: result: 5000000050000000 ; run time by test InfoControllerTestWithDb: 613ms; clear run time: 173ms
     *         long someResult = LongStream.rangeClosed(1L, limit2).sum();
     *         limit2: result: 5000000050000000 ; run time by test InfoControllerTestWithDb: 511ms; clear run time: 0ms
     *         long someResult = LongStream.of((1L + limit2) * limit2 / 2L).sum();
     *         </p>
     *         ******************Experiments with limit4 ***************************
     *         <p></p>
     *         limit4: result: 45000000150000000 ; run time by test InfoControllerTestWithDb: 1s 855ms; clear run time: 1156ms
     *         long someResult = Stream.iterate(1L, a -> a +1L) .limit(limit2) .reduce(0L, (a, b) -> a + b );
     *         limit4 OutOfMemory
     *         long someResult = Stream.iterate(1L, a -> a + 1L).limit(limit4).parallel().reduce(0L, (a, b) -> a + b);
     *         limit4 OutOfMemory
     *         long someResult = Stream.iterate(1L, a -> a + 1L).parallel().limit(limit4).reduce(0L, (a, b) -> a + b);
     *         limit4: result: 45000000150000000 ; run time by test InfoControllerTestWithDb: 741ms; clear run time: 65ms
     *         long someResult = LongStream.rangeClosed(1L, limit4).parallel().sum();
     *         limit4: result: 45000000150000000 ; run time by test InfoControllerTestWithDb: 1s 179ms; clear run time: 571ms
     *         long someResult = LongStream.rangeClosed(1L, limit4).sum();
     *         </p>
     *         ******************Experiments with limit5 *****************************
     *         <p>
     *         limit5: result: 31250000125000000 ; run time by test InfoControllerTestWithDb: 4s 297ms; clear run time: 3641ms
     *         long someResult = Stream.iterate(1L, a -> a +1L) .limit(limit5) .reduce(0L, (a, b) -> a + b );
     *         limit5: OutOfMemory
     *         long someResult = Stream.iterate(1L, a -> a + 1L).limit(limit5).parallel().reduce(0L, (a, b) -> a + b);
     *         limit5: OutOfMemory
     *         long someResult = Stream.iterate(1L, a -> a + 1L).parallel().limit(limit5).reduce(0L, (a, b) -> a + b);
     *         limit5: result: 31250000125000000 ; run time by test InfoControllerTestWithDb: 844ms; clear run time: 62ms
     *         long someResult = LongStream.rangeClosed(1L, limit5).parallel().sum();
     *         limit5: result: 31250000125000000 ; run time by test InfoControllerTestWithDb: 1s 145ms; clear run time: 466ms
     *         long someResult = LongStream.rangeClosed(1L, limit5).sum();
     *         </p>
     *         **********************Experiments with limit6 **************************
     *         <p>
     *         limit6: result: 20000000100000000 ; run time by test InfoControllerTestWithDb: 3s 449ms; clear run time: 2507ms
     *         long someResult = Stream.iterate(1L, a -> a +1L) .limit(limit6) .reduce(0L, (a, b) -> a + b );
     *         limit6: OutOfMemory
     *         long someResult = Stream.iterate(1L, a -> a + 1L).limit(limit6).parallel().reduce(0L, (a, b) -> a + b);
     *         limit6: OutOfMemory
     *         long someResult = Stream.iterate(1L, a -> a + 1L).parallel().limit(limit6).reduce(0L, (a, b) -> a + b);
     *         limit6: result: 20000000100000000 ; run time by test InfoControllerTestWithDb: 550ms; clear run time: 32ms
     *         long someResult = LongStream.rangeClosed(1L, limit6).parallel().sum();
     *         limit6: result: 20000000100000000 ; run time by test InfoControllerTestWithDb: 1s 152ms; clear run time: 438ms
     *         long someResult = LongStream.rangeClosed(1L, limit6).sum();
     *         </p>
     *         **********************Experiments with limit7 **************************
     *         <p>
     *         limit7: result: 8711999934000000 ; run time by test InfoControllerTestWithDb: 1s 541ms; clear run time: 1073ms
     *         long someResult = Stream.iterate(1L, a -> a +1L) .limit(limit7) .reduce(0L, (a, b) -> a + b );
     *         limit7: result: 8711999934000000 ; run time by test InfoControllerTestWithDb: 10s 680ms; clear run time: 10006ms
     *         long someResult = Stream.iterate(1L, a -> a + 1L).limit(limit7).parallel().reduce(0L, (a, b) -> a + b);
     *         limit7: result: 8711999934000000 ; run time by test InfoControllerTestWithDb: 8s 124ms; clear run time: 7646ms
     *         long someResult = Stream.iterate(1L, a -> a + 1L).parallel().limit(limit7).reduce(0L, (a, b) -> a + b);
     *         limit7: result: 8711999934000000 ; run time by test InfoControllerTestWithDb: 533ms; clear run time: 52ms
     *         long someResult = LongStream.rangeClosed(1L, limit7).parallel().sum();
     *         limit7: result: 8711999934000000 ; run time by test InfoControllerTestWithDb: 728ms; clear run time: 205ms
     *         long someResult = LongStream.rangeClosed(1L, limit7).sum();
     *         </p>
     *         **********************Experiments with limit2 с ограничением числа потоков до 4 **************************
     *         <p>
     *         limit2: result: 5000000050000000 ; run time by test InfoControllerTestWithDb: 3s 422ms; clear run time: 2822ms
     *         long someResult = pool.submit(() -> Stream.iterate(1L, a -> a + 1L).limit(limit2).parallel().reduce(0L, (a, b) -> a + b)).get();
     *         limit2: result: 5000000050000000 ; run time by test InfoControllerTestWithDb: 3s 98ms; clear run time: 2550ms
     *         long someResult = pool.submit(() -> Stream.iterate(1L, a -> a + 1L).parallel().limit(limit2).reduce(0L, (a, b) -> a + b)).get();
     *         limit2: result: 5000000050000000 ; run time by test InfoControllerTestWithDb: 515ms; clear run time: 33ms
     *         long someResult = pool.submit(() -> LongStream.rangeClosed(1L, limit2).parallel().sum()).get();
     *         </p>
     *         <p>
     *         Тесты проводились на следующей конфигурации ПК:
     *         Notebook HUAWEI MateBook D 14 MDG-X
     *         Win 11 Pro
     *         CPU: 13th Gen Intel(R) Core(TM) i5-13420H (2.10 GHz)
     *         RAM: 16GB DDR5
     *         M.2 SSD: 500GB
     *         </p>
     *         <p>
     *         На текущей конфигурации распараллеливание стрима из учебного пособия крайне малоэффективно из-за
     *         простоты вычисляемого выражения и большого объема генерируемых объектов. Ни один из вариантов параллельного
     *          стрима не смог увеличить производительность вычисляемого выражения.
     *          Ощутимый прирост производительности наблюдался только при увеличении конечного вычисляемого значения с
     *         1 000 000 до 100 000 000 и только в таком варианте(30ms-34ms):
     *         long someResult = pool.submit(() -> LongStream.rangeClosed(1L, limit2).parallel().sum()).get();
     *         т.к. он работает с примитивами long. Вариант со Stream.iterate.parallel генерирует объекты типа Integer или Long
     *         что из-за слишком простого вычисляемого выражения существенно снижает производительность и нивелирует
     *         выигрыш от распараллеливания. Даже ограничение по количеству потоков до 4-х не оказывает большого влияния.
     *         Наибольшей производительности удалось добиться при ограничении потоков до 8 в следующем случае:
     *         limit2: result: 5000000050000000 ; run time by test InfoControllerTestWithDb: 795ms; clear run time: 29ms
     *         long someResult = pool.submit(() -> LongStream.rangeClosed(1L, limit2).parallel().sum()).get();
     *         Но при увеличении потоков с 9 и выше производительность начинает падать. 44ms время выполнения.
     *         </p>
     */
    //GET http://localhost:port/info/some_calc
    @GetMapping("/some_calc")
    public ResponseEntity<Long> getSomeResult() throws Exception{
        ForkJoinPool pool = new ForkJoinPool(8);
        long limit2 = 100000000L;

        long start = System.currentTimeMillis();
        long someResult = pool.submit(() -> LongStream.rangeClosed(1L, limit2).parallel().sum()).get();
        pool.shutdown();
        long end = System.currentTimeMillis();
        logger.info("Time: {} ms", end - start);
        return ResponseEntity.ok(someResult);
    }
}
